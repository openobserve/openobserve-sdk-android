/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.webview.internal.log

import com.openobserve.android.api.InternalLogger
import com.openobserve.android.api.context.OpenObserveContext
import com.openobserve.android.api.feature.Feature
import com.openobserve.android.api.feature.FeatureSdkCore
import com.openobserve.android.api.storage.DataWriter
import com.openobserve.android.api.storage.EventType
import com.openobserve.android.core.sampling.RateBasedSampler
import com.openobserve.android.core.sampling.Sampler
import com.openobserve.android.log.LogAttributes
import com.openobserve.android.webview.internal.WebViewEventConsumer
import com.openobserve.android.webview.internal.rum.WebViewRumEventContextProvider
import com.openobserve.android.webview.internal.rum.domain.RumContext
import com.google.gson.JsonObject

internal class WebViewLogEventConsumer(
    private val sdkCore: FeatureSdkCore,
    internal val userLogsWriter: DataWriter<JsonObject>,
    private val rumContextProvider: WebViewRumEventContextProvider,
    sampleRate: Float
) : WebViewEventConsumer<Pair<JsonObject, String>> {

    private val sampler: Sampler<Unit> = RateBasedSampler(sampleRate)

    override fun consume(event: Pair<JsonObject, String>) {
        if (event.second == USER_LOG_EVENT_TYPE) {
            if (sampler.sample(Unit)) {
                sdkCore.getFeature(WebViewLogsFeature.WEB_LOGS_FEATURE_NAME)
                    ?.withWriteContext(
                        withFeatureContexts = setOf(Feature.RUM_FEATURE_NAME)
                    ) { ooContext, writeScope ->
                        val rumContext = rumContextProvider.getRumContext(ooContext)
                        writeScope {
                            val mappedEvent = map(event.first, ooContext, rumContext)
                            userLogsWriter.write(it, mappedEvent, EventType.DEFAULT)
                        }
                    }
            }
        }
    }

    private fun map(
        event: JsonObject,
        ooContext: OpenObserveContext,
        rumContext: RumContext?
    ): JsonObject {
        addDdTags(event, ooContext)
        correctDate(event, ooContext)
        addAnonymousId(event, ooContext)
        if (rumContext != null) {
            event.addProperty(LogAttributes.RUM_APPLICATION_ID, rumContext.applicationId)
            event.addProperty(LogAttributes.RUM_SESSION_ID, rumContext.sessionId)
        }
        return event
    }

    private fun addAnonymousId(event: JsonObject, ooContext: OpenObserveContext) {
        val anonymousId = ooContext.userInfo.anonymousId ?: return
        val usr = event.getAsJsonObject(USR_KEY_NAME) ?: JsonObject()
        usr.addProperty(ANONYMOUS_ID_KEY_NAME, anonymousId)
        event.add(USR_KEY_NAME, usr)
    }

    private fun correctDate(event: JsonObject, ooContext: OpenObserveContext) {
        try {
            event.get(DATE_KEY_NAME)?.asLong?.let {
                event.addProperty(
                    DATE_KEY_NAME,
                    it + ooContext.time.serverTimeOffsetMs
                )
            }
        } catch (e: ClassCastException) {
            sdkCore.internalLogger.log(
                InternalLogger.Level.ERROR,
                listOf(InternalLogger.Target.MAINTAINER, InternalLogger.Target.TELEMETRY),
                { JSON_PARSING_ERROR_MESSAGE },
                e
            )
        } catch (e: IllegalStateException) {
            sdkCore.internalLogger.log(
                InternalLogger.Level.ERROR,
                listOf(InternalLogger.Target.MAINTAINER, InternalLogger.Target.TELEMETRY),
                { JSON_PARSING_ERROR_MESSAGE },
                e
            )
        } catch (e: NumberFormatException) {
            sdkCore.internalLogger.log(
                InternalLogger.Level.ERROR,
                listOf(InternalLogger.Target.MAINTAINER, InternalLogger.Target.TELEMETRY),
                { JSON_PARSING_ERROR_MESSAGE },
                e
            )
        } catch (e: UnsupportedOperationException) {
            sdkCore.internalLogger.log(
                InternalLogger.Level.ERROR,
                listOf(InternalLogger.Target.MAINTAINER, InternalLogger.Target.TELEMETRY),
                { JSON_PARSING_ERROR_MESSAGE },
                e
            )
        }
    }

    private fun addDdTags(event: JsonObject, ooContext: OpenObserveContext) {
        val sdkDdTags = mapOf(
            LogAttributes.APPLICATION_VERSION to ooContext.version,
            LogAttributes.ENV to ooContext.env,
            LogAttributes.VARIANT to ooContext.variant,
            LogAttributes.SERVICE to ooContext.service
        )
        val eventDdTags = try {
            event.get(OOTAGS_KEY_NAME)?.asString?.let {
                it.split(OOTAGS_SEPARATOR)
                    .mapNotNull { tag ->
                        @Suppress("UnsafeThirdPartyFunctionCall") // safe indexOf invocation
                        val splitIndex = tag.indexOf(":")
                        if (splitIndex == -1 || splitIndex == tag.lastIndex) {
                            null
                        } else {
                            @Suppress("UnsafeThirdPartyFunctionCall") // safe substring invocations
                            tag.substring(0, splitIndex) to tag.substring(splitIndex + 1)
                        }
                    }
                    .associate { it }
            }.orEmpty()
        } catch (e: ClassCastException) {
            sdkCore.internalLogger.log(
                InternalLogger.Level.ERROR,
                listOf(InternalLogger.Target.MAINTAINER, InternalLogger.Target.TELEMETRY),
                { JSON_PARSING_ERROR_MESSAGE },
                e
            )
            emptyMap<String, String>()
        } catch (e: IllegalStateException) {
            sdkCore.internalLogger.log(
                InternalLogger.Level.ERROR,
                listOf(InternalLogger.Target.MAINTAINER, InternalLogger.Target.TELEMETRY),
                { JSON_PARSING_ERROR_MESSAGE },
                e
            )
            emptyMap<String, String>()
        } catch (e: UnsupportedOperationException) {
            sdkCore.internalLogger.log(
                InternalLogger.Level.ERROR,
                listOf(InternalLogger.Target.MAINTAINER, InternalLogger.Target.TELEMETRY),
                { JSON_PARSING_ERROR_MESSAGE },
                e
            )
            emptyMap<String, String>()
        }
        event.addProperty(
            OOTAGS_KEY_NAME,
            (eventDdTags + sdkDdTags)
                .map { "${it.key}:${it.value}" }
                .joinToString(OOTAGS_SEPARATOR)
        )
    }

    companion object {
        const val OOTAGS_SEPARATOR = ","
        const val OOTAGS_KEY_NAME = "ootags"
        const val DATE_KEY_NAME = "date"
        const val USER_LOG_EVENT_TYPE = "log"
        const val INTERNAL_LOG_EVENT_TYPE = "internal_log"
        const val USR_KEY_NAME = "usr"
        const val ANONYMOUS_ID_KEY_NAME = "anonymous_id"
        const val JSON_PARSING_ERROR_MESSAGE = "The bundled web log event could not be deserialized"
        val LOG_EVENT_TYPES = setOf(USER_LOG_EVENT_TYPE)
        internal const val DEFAULT_SAMPLE_RATE = 100f
    }
}
