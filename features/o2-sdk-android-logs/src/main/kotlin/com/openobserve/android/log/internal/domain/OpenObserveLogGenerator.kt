/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.log.internal.domain

import com.openobserve.android.api.InternalLogger
import com.openobserve.android.api.context.AccountInfo
import com.openobserve.android.api.context.OpenObserveContext
import com.openobserve.android.api.context.DeviceInfo
import com.openobserve.android.api.context.DeviceType
import com.openobserve.android.api.context.NetworkInfo
import com.openobserve.android.api.context.UserInfo
import com.openobserve.android.api.feature.Feature
import com.openobserve.android.core.feature.event.ThreadDump
import com.openobserve.android.log.LogAttributes
import com.openobserve.android.log.internal.utils.buildLogDateFormat
import com.openobserve.android.log.model.LogEvent
import java.util.Date
import java.util.Locale

@Suppress("TooManyFunctions")
internal class OpenObserveLogGenerator(
    /**
     * Custom service name. If not provided, value will be taken from [OpenObserveContext].
     */
    internal val serviceName: String? = null,
    private val internalLogger: InternalLogger
) : LogGenerator {

    private val simpleDateFormat = buildLogDateFormat()

    @Suppress("LongParameterList")
    override fun generateLog(
        level: Int,
        message: String,
        throwable: Throwable?,
        attributes: Map<String, Any?>,
        tags: Set<String>,
        timestamp: Long,
        threadName: String,
        ooContext: OpenObserveContext,
        attachNetworkInfo: Boolean,
        loggerName: String,
        bundleWithTraces: Boolean,
        bundleWithRum: Boolean,
        userInfo: UserInfo?,
        accountInfo: AccountInfo?,
        networkInfo: NetworkInfo?,
        threads: List<ThreadDump>
    ): LogEvent {
        val mutableAttributes = attributes.toMutableMap()
        val error = throwable?.let {
            val fingerprint = mutableAttributes.remove(LogAttributes.ERROR_FINGERPRINT) as? String
            val kind = it.javaClass.canonicalName ?: it.javaClass.simpleName
            LogEvent.Error(
                kind = kind,
                stack = it.stackTraceToString(),
                message = it.message,
                fingerprint = fingerprint,
                threads = threads.map { thread ->
                    LogEvent.Thread(
                        name = thread.name,
                        crashed = thread.crashed,
                        stack = thread.stack,
                        state = thread.state
                    )
                }.ifEmpty { null }
            )
        }
        return internalGenerateLog(
            level,
            message,
            error,
            mutableAttributes,
            tags,
            timestamp,
            threadName,
            ooContext,
            attachNetworkInfo,
            loggerName,
            bundleWithTraces,
            bundleWithRum,
            userInfo,
            accountInfo,
            networkInfo
        )
    }

    override fun generateLog(
        level: Int,
        message: String,
        errorKind: String?,
        errorMessage: String?,
        errorStack: String?,
        attributes: Map<String, Any?>,
        tags: Set<String>,
        timestamp: Long,
        threadName: String,
        ooContext: OpenObserveContext,
        attachNetworkInfo: Boolean,
        loggerName: String,
        bundleWithTraces: Boolean,
        bundleWithRum: Boolean,
        userInfo: UserInfo?,
        accountInfo: AccountInfo?,
        networkInfo: NetworkInfo?
    ): LogEvent {
        val mutableAttributes = attributes.toMutableMap()
        val error = if (errorKind != null || errorMessage != null || errorStack != null) {
            val sourceType = mutableAttributes.remove(LogAttributes.SOURCE_TYPE) as? String
            val fingerprint = mutableAttributes.remove(LogAttributes.ERROR_FINGERPRINT) as? String
            LogEvent.Error(
                kind = errorKind,
                message = errorMessage,
                stack = errorStack,
                fingerprint = fingerprint,
                sourceType = sourceType
            )
        } else {
            null
        }
        return internalGenerateLog(
            level,
            message,
            error,
            mutableAttributes,
            tags,
            timestamp,
            threadName,
            ooContext,
            attachNetworkInfo,
            loggerName,
            bundleWithTraces,
            bundleWithRum,
            userInfo,
            accountInfo,
            networkInfo
        )
    }

    // region Internal

    @Suppress("LongParameterList")
    private fun internalGenerateLog(
        level: Int,
        message: String,
        error: LogEvent.Error?,
        attributes: Map<String, Any?>,
        tags: Set<String>,
        timestamp: Long,
        threadName: String,
        ooContext: OpenObserveContext,
        attachNetworkInfo: Boolean,
        loggerName: String,
        bundleWithTraces: Boolean,
        bundleWithRum: Boolean,
        userInfo: UserInfo?,
        accountInfo: AccountInfo?,
        networkInfo: NetworkInfo?
    ): LogEvent {
        val resolvedTimestamp = timestamp + ooContext.time.serverTimeOffsetMs
        val combinedAttributes = resolveAttributes(
            ooContext,
            attributes,
            bundleWithTraces,
            threadName,
            bundleWithRum
        )
        val formattedDate = synchronized(simpleDateFormat) {
            @Suppress("UnsafeThirdPartyFunctionCall") // NPE cannot happen here
            simpleDateFormat.format(Date(resolvedTimestamp))
        }
        val deviceInfo = ooContext.deviceInfo
        val combinedTags = resolveTags(ooContext, tags)
        val usr = resolveUserInfo(ooContext, userInfo)
        val account = resolveAccountInfo(ooContext, accountInfo)
        val network = if (networkInfo != null || attachNetworkInfo) {
            resolveNetworkInfo(ooContext, networkInfo)
        } else {
            null
        }
        val loggerInfo = LogEvent.Logger(
            name = loggerName,
            threadName = threadName,
            version = ooContext.sdkVersion
        )
        return LogEvent(
            service = serviceName ?: ooContext.service,
            status = resolveLogLevelStatus(level),
            message = message,
            date = formattedDate,
            // TODO RUM-3832 If NDK crash, the it should be a value from previous build
            //  (or whatever distinguishes debug symbols for native libs)
            buildId = ooContext.appBuildId,
            error = error,
            logger = loggerInfo,
            oo = LogEvent.Oo(
                device = LogEvent.OoDevice(
                    architecture = deviceInfo.architecture
                )
            ),
            usr = usr,
            account = account,
            network = network,
            o2tags = combinedTags.joinToString(separator = ","),
            additionalProperties = combinedAttributes,
            os = resolveOsInfo(deviceInfo),
            device = resolveDeviceInfo(deviceInfo),
            buildVersion = ooContext.versionCode.toString()
        )
    }

    private fun resolveOsInfo(deviceInfo: DeviceInfo) = LogEvent.Os(
        name = deviceInfo.osName,
        version = deviceInfo.osVersion,
        versionMajor = deviceInfo.osMajorVersion
    )

    private fun resolveDeviceInfo(deviceInfo: DeviceInfo) = LogEvent.LogEventDevice(
        type = resolveDeviceType(deviceInfo.deviceType),
        name = deviceInfo.deviceName,
        model = deviceInfo.deviceModel,
        brand = deviceInfo.deviceBrand,
        architecture = deviceInfo.architecture,
        logicalCpuCount = deviceInfo.logicalCpuCount,
        totalRam = deviceInfo.totalRam,
        isLowRam = deviceInfo.isLowRam
    )

    private fun resolveDeviceType(deviceType: DeviceType): LogEvent.Type = when (deviceType) {
        DeviceType.MOBILE -> LogEvent.Type.MOBILE
        DeviceType.TABLET -> LogEvent.Type.TABLET
        DeviceType.TV -> LogEvent.Type.TV
        DeviceType.DESKTOP -> LogEvent.Type.DESKTOP
        DeviceType.GAMING_CONSOLE -> LogEvent.Type.GAMING_CONSOLE
        DeviceType.BOT -> LogEvent.Type.BOT
        DeviceType.OTHER -> LogEvent.Type.OTHER
    }

    private fun envTag(ooContext: OpenObserveContext): String? {
        val envName = ooContext.env
        return if (envName.isNotEmpty()) {
            "${LogAttributes.ENV}:$envName"
        } else {
            null
        }
    }

    private fun appVersionTag(ooContext: OpenObserveContext): String? {
        val appVersion = ooContext.version
        return if (appVersion.isNotEmpty()) {
            "${LogAttributes.APPLICATION_VERSION}:$appVersion"
        } else {
            null
        }
    }

    private fun variantTag(ooContext: OpenObserveContext): String? {
        val variant = ooContext.variant
        return if (variant.isNotEmpty()) {
            "${LogAttributes.VARIANT}:$variant"
        } else {
            null
        }
    }

    private fun serviceTag(ooContext: OpenObserveContext): String? {
        val service = serviceName ?: ooContext.service
        return if (service.isNotEmpty()) {
            "${LogAttributes.SERVICE}:$service"
        } else {
            null
        }
    }

    private fun resolveNetworkInfo(
        ooContext: OpenObserveContext,
        networkInfo: NetworkInfo?
    ): LogEvent.Network {
        return with(networkInfo ?: ooContext.networkInfo) {
            LogEvent.Network(
                LogEvent.Client(
                    simCarrier = resolveSimCarrier(this),
                    signalStrength = strength?.toString(),
                    downlinkKbps = downKbps?.toString(),
                    uplinkKbps = upKbps?.toString(),
                    connectivity = connectivity.toString()
                )
            )
        }
    }

    private fun resolveUserInfo(ooContext: OpenObserveContext, userInfo: UserInfo?): LogEvent.Usr {
        return with(userInfo ?: ooContext.userInfo) {
            LogEvent.Usr(
                anonymousId = anonymousId,
                name = name,
                email = email,
                id = id,
                additionalProperties = additionalProperties.toMutableMap()
            )
        }
    }

    private fun resolveAccountInfo(
        ooContext: OpenObserveContext,
        accountInfo: AccountInfo?
    ): LogEvent.Account? {
        return (accountInfo ?: ooContext.accountInfo)?.let {
            LogEvent.Account(
                id = it.id,
                name = it.name,
                additionalProperties = it.extraInfo.toMutableMap()
            )
        }
    }

    private fun resolveTags(
        ooContext: OpenObserveContext,
        tags: Set<String>
    ): MutableSet<String> {
        val combinedTags = tags
            .filter {
                val key = it.split(":").firstOrNull()
                if (key != null && key in RESERVED_TAG_KEYS) {
                    internalLogger.log(
                        InternalLogger.Level.WARN,
                        InternalLogger.Target.USER,
                        { RESERVED_TAG_DROPPED_WARNING.format(Locale.US, it) },
                        onlyOnce = true
                    )
                    false
                } else {
                    true
                }
            }
            .toMutableSet()

        // when doing changes below check also OpenObserveDataConstraints.reservedTagKeys
        envTag(ooContext)?.let {
            combinedTags.add(it)
        }
        appVersionTag(ooContext)?.let {
            combinedTags.add(it)
        }
        variantTag(ooContext)?.let {
            combinedTags.add(it)
        }
        serviceTag(ooContext)?.let {
            combinedTags.add(it)
        }

        return combinedTags
    }

    private fun resolveAttributes(
        ooContext: OpenObserveContext,
        attributes: Map<String, Any?>,
        bundleWithTraces: Boolean,
        threadName: String,
        bundleWithRum: Boolean
    ): MutableMap<String, Any?> {
        val combinedAttributes = mutableMapOf<String, Any?>().apply { putAll(attributes) }
        if (bundleWithTraces) {
            ooContext.featuresContext[Feature.TRACING_FEATURE_NAME]?.let {
                val threadLocalContext = it["context@$threadName"] as? Map<*, *>
                if (threadLocalContext != null) {
                    combinedAttributes[LogAttributes.DD_TRACE_ID] = threadLocalContext["trace_id"]
                    combinedAttributes[LogAttributes.DD_SPAN_ID] = threadLocalContext["span_id"]
                }
            }
        }
        if (bundleWithRum) {
            ooContext.featuresContext[Feature.RUM_FEATURE_NAME]?.let {
                combinedAttributes[LogAttributes.RUM_APPLICATION_ID] = it["application_id"]
                combinedAttributes[LogAttributes.RUM_SESSION_ID] = it["session_id"]
                combinedAttributes[LogAttributes.RUM_VIEW_ID] = it["view_id"]
                combinedAttributes[LogAttributes.RUM_ACTION_ID] = it["action_id"]
            }
        }
        return combinedAttributes
    }

    @Suppress("DEPRECATION")
    private fun resolveLogLevelStatus(level: Int): LogEvent.Status {
        return when (level) {
            android.util.Log.ASSERT -> LogEvent.Status.CRITICAL
            android.util.Log.ERROR -> LogEvent.Status.ERROR
            android.util.Log.WARN -> LogEvent.Status.WARN
            android.util.Log.INFO -> LogEvent.Status.INFO
            android.util.Log.DEBUG -> LogEvent.Status.DEBUG
            android.util.Log.VERBOSE -> LogEvent.Status.TRACE
            OpenObserveLogGenerator.CRASH -> LogEvent.Status.EMERGENCY
            else -> LogEvent.Status.DEBUG
        }
    }

    private fun resolveSimCarrier(networkInfo: NetworkInfo): LogEvent.SimCarrier? {
        return if (networkInfo.carrierId != null || networkInfo.carrierName != null) {
            LogEvent.SimCarrier(
                id = networkInfo.carrierId?.toString(),
                name = networkInfo.carrierName
            )
        } else {
            null
        }
    }

    // endregion

    companion object {
        internal const val ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        internal const val CRASH: Int = 9
        internal const val RESERVED_TAG_DROPPED_WARNING = "User-provided tag %s is dropped," +
            " because it matches reserved tag key"
        internal val RESERVED_TAG_KEYS = setOf(
            LogAttributes.ENV,
            LogAttributes.APPLICATION_VERSION,
            LogAttributes.VARIANT,
            LogAttributes.SERVICE
        )
    }
}
