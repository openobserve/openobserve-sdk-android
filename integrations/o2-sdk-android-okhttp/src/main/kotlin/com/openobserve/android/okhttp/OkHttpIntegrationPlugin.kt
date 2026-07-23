/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */
package com.openobserve.android.okhttp

import com.openobserve.android.api.InternalLogger
import com.openobserve.android.api.feature.FeatureSdkCore
import com.openobserve.android.api.logToUser
import com.openobserve.android.internal.telemetry.InternalTelemetryEvent.ApiUsage.NetworkInstrumentation.LibraryType
import com.openobserve.android.okhttp.internal.ApmInstrumentationOkHttpAdapter
import com.openobserve.android.okhttp.internal.CompositeEventListener
import com.openobserve.android.okhttp.internal.RegistryTrackingEventListener
import com.openobserve.android.okhttp.internal.RequestTracingStateRegistry
import com.openobserve.android.okhttp.internal.RumInstrumentationOkHttpAdapter
import com.openobserve.android.okhttp.internal.RumInstrumentationTimingsCounter
import com.openobserve.android.rum.ExperimentalRumApi
import com.openobserve.android.rum._RumInternalProxy
import com.openobserve.android.rum.configuration.RumNetworkInstrumentationConfiguration
import com.openobserve.android.rum.internal.net.RumNetworkInstrumentation
import com.openobserve.android.trace.ApmNetworkInstrumentationConfiguration
import com.openobserve.android.trace.ApmNetworkTracingScope
import com.openobserve.android.trace.ExperimentalTraceApi
import com.openobserve.android.trace.internal.ApmNetworkInstrumentation
import com.openobserve.android.trace.internal._TraceInternalProxy
import okhttp3.EventListener
import okhttp3.OkHttpClient

/**
 * Configures OpenObserve network instrumentation on this [OkHttpClient.Builder].
 * Returns a [OkHttpIntegrationPlugin] that applies the configured OpenObserve instrumentation when
 * [OkHttpIntegrationPlugin.build] is called.
 *
 * @param apmInstrumentationConfiguration optional APM tracing configuration. When provided, trace spans
 * will be created for HTTP requests and tracing headers will be injected for first-party hosts.
 * @param rumInstrumentationConfiguration optional RUM configuration. When provided, HTTP requests
 * will be automatically tracked as RUM resources with timing information.
 */
@ExperimentalRumApi
@ExperimentalTraceApi
@Suppress("PackageNameVisibility")
internal fun OkHttpClient.Builder.configureOpenObserveInstrumentation(
    apmInstrumentationConfiguration: ApmNetworkInstrumentationConfiguration?,
    rumInstrumentationConfiguration: RumNetworkInstrumentationConfiguration?
) = OkHttpIntegrationPlugin(this, rumInstrumentationConfiguration, apmInstrumentationConfiguration)

/**
 * Plugin that applies OpenObserve RUM and APM instrumentation to an [OkHttpClient.Builder].
 * Use [OkHttpClient.Builder.configureOpenObserveInstrumentation] after finishing builder
 * configuration, then call [build] to produce an instrumented [OkHttpClient].
 */
@ExperimentalRumApi
@ExperimentalTraceApi
class OkHttpIntegrationPlugin internal constructor(
    private val delegate: OkHttpClient.Builder,
    private val rumConfiguration: RumNetworkInstrumentationConfiguration?,
    private val apmConfiguration: ApmNetworkInstrumentationConfiguration?
) {
    private var builtClient: OkHttpClient? = null

    /**
     * Builds the [OkHttpClient] with OpenObserve instrumentation applied.
     * @return an instrumented [OkHttpClient] instance.
     */
    @Suppress("ReturnCount")
    @Synchronized
    fun build(): OkHttpClient {
        builtClient?.let { return it }
        val rumInstrumentation = rumConfiguration?.createRumInstrumentation()
        val apmInstrumentation = apmConfiguration?.createApmInstrumentation(rumInstrumentation != null)

        val internalLogger = requireInternalLogger(rumInstrumentation, apmInstrumentation)

        if (apmInstrumentation == null && rumInstrumentation == null) {
            internalLogger.logToUser(InternalLogger.Level.WARN) {
                "OpenObserve network instrumentation configuration is incorrect: " +
                    "both RUM and APM instrumentations are null."
            }
            return delegate.buildIdempotently()
        }

        val registry = RequestTracingStateRegistry(internalLogger)

        delegate.wrapExistingEventListenerFactory(
            registry,
            rumInstrumentation
        )

        if (rumInstrumentation != null) {
            @Suppress("UnsafeThirdPartyFunctionCall") // IllegalArgumentException cannot happen here
            delegate.addInterceptor(
                RumInstrumentationOkHttpAdapter(
                    registry,
                    rumInstrumentation,
                    apmConfiguration?.createDistributedTracingInstrumentation(true)
                )
            )
        }

        if (apmInstrumentation != null) {
            val interceptor = ApmInstrumentationOkHttpAdapter(apmInstrumentation, registry)
            @Suppress("UnsafeThirdPartyFunctionCall") // IllegalArgumentException cannot happen here
            when (apmInstrumentation.networkTracingScope) {
                ApmNetworkTracingScope.ALL -> delegate.addNetworkInterceptor(interceptor)
                ApmNetworkTracingScope.EXCLUDE_INTERNAL_REDIRECTS -> delegate.addInterceptor(interceptor)
            }
        }

        return delegate.buildIdempotently()
    }

    private fun OkHttpClient.Builder.buildIdempotently(): OkHttpClient = build().also { builtClient = it }

    internal companion object {
        internal const val ORIGIN_RUM = "rum"
        internal const val OKHTTP_NETWORK_INSTRUMENTATION_NAME = "okhttp"

        private fun OkHttpClient.Builder.wrapExistingEventListenerFactory(
            registry: RequestTracingStateRegistry,
            rumNetworkInstrumentation: RumNetworkInstrumentation?
        ) {
            val userEventListenerFactory: EventListener.Factory = build().eventListenerFactory
            val innerFactories = listOfNotNull(
                rumNetworkInstrumentation?.let {
                    RumInstrumentationTimingsCounter.Factory(it, registry)
                },
                userEventListenerFactory
            )

            val coreFactory = when (innerFactories.size) {
                0 -> {
                    @Suppress("UnsafeThirdPartyFunctionCall") // NPE cannot happen here
                    EventListener.Factory { EventListener.NONE }
                }

                1 -> {
                    // NoSuchElementException/IllegalArgumentException cannot happen here
                    @Suppress("UnsafeThirdPartyFunctionCall")
                    innerFactories.single()
                }

                else -> {
                    CompositeEventListener.Factory(delegates = innerFactories)
                }
            }

            @Suppress("UnsafeThirdPartyFunctionCall") // NPE cannot happen here
            eventListenerFactory(RegistryTrackingEventListener.Factory(registry, coreFactory))
        }

        private fun requireInternalLogger(
            rumNetworkInstrumentation: RumNetworkInstrumentation?,
            apmInstrumentation: ApmNetworkInstrumentation?
        ): InternalLogger = (
            (rumNetworkInstrumentation?.sdkCore as? FeatureSdkCore)?.internalLogger
                ?: (apmInstrumentation?.sdkCore as? FeatureSdkCore)?.internalLogger
                ?: InternalLogger.UNBOUND
            )

        private fun RumNetworkInstrumentationConfiguration.createRumInstrumentation(): RumNetworkInstrumentation =
            _RumInternalProxy.createRumNetworkInstrumentation(
                OKHTTP_NETWORK_INSTRUMENTATION_NAME,
                LibraryType.OKHTTP,
                this
            )

        private fun ApmNetworkInstrumentationConfiguration.createApmInstrumentation(
            rumInstrumentationExists: Boolean
        ) = if (!isHeaderPropagationOnly() || !rumInstrumentationExists) {
            _TraceInternalProxy.createApmNetworkInstrumentation(
                OKHTTP_NETWORK_INSTRUMENTATION_NAME,
                this
            )
        } else {
            null
        }

        private fun ApmNetworkInstrumentationConfiguration.createDistributedTracingInstrumentation(
            rumInstrumentationExists: Boolean
        ) = if (rumInstrumentationExists) {
            _TraceInternalProxy.createApmNetworkInstrumentation(
                OKHTTP_NETWORK_INSTRUMENTATION_NAME,
                configuration = copy()
                    .setHeaderPropagationOnly()
                    .setTraceOrigin(ORIGIN_RUM, replace = true)
                    .setTraceScope(ApmNetworkTracingScope.EXCLUDE_INTERNAL_REDIRECTS)
            )
        } else {
            null
        }
    }
}
