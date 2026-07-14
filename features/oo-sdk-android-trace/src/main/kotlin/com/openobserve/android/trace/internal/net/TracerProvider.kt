/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.trace.internal.net

import com.openobserve.android.api.InternalLogger
import com.openobserve.android.api.SdkCore
import com.openobserve.android.api.feature.Feature
import com.openobserve.android.api.logToUser
import com.openobserve.android.core.InternalSdkCore
import com.openobserve.android.trace.TracingHeaderType
import com.openobserve.android.trace.api.tracer.OpenObserveTracer
import java.util.Locale
import java.util.concurrent.atomic.AtomicReference

internal class TracerProvider internal constructor(
    private val localTracerFactory: (SdkCore, Set<TracingHeaderType>) -> OpenObserveTracer,
    private val globalTracerProvider: () -> OpenObserveTracer?
) {
    private val localTracerReference: AtomicReference<OpenObserveTracer> = AtomicReference()

    @Synchronized
    fun provideTracer(
        sdkCore: InternalSdkCore,
        localHeaderTypes: Set<TracingHeaderType>,
        networkingLibraryName: String
    ): OpenObserveTracer? {
        val tracingFeature = sdkCore.getFeature(Feature.TRACING_FEATURE_NAME)
        val globalTracerInstance = globalTracerProvider.invoke()
        return when {
            tracingFeature == null -> {
                sdkCore.internalLogger.logToUser(
                    level = InternalLogger.Level.WARN,
                    onlyOnce = true
                ) { WARNING_TRACING_DISABLED.format(Locale.US, networkingLibraryName) }
                null
            }

            globalTracerInstance != null -> {
                // clear the localTracer reference if any
                localTracerReference.set(null)
                globalTracerInstance
            }

            else -> {
                // only register once
                if (localTracerReference.get() == null) {
                    @Suppress("UnsafeThirdPartyFunctionCall") // internal safe call
                    val globalHeaderTypes = sdkCore.firstPartyHostResolver.getAllHeaderTypes()
                    val allHeaders = localHeaderTypes.plus(globalHeaderTypes)
                    localTracerReference.compareAndSet(null, localTracerFactory(sdkCore, allHeaders))
                    sdkCore.internalLogger.logToUser(InternalLogger.Level.WARN) {
                        WARNING_DEFAULT_TRACER.format(Locale.US, networkingLibraryName)
                    }
                }
                localTracerReference.get()
            }
        }
    }

    internal companion object {
        const val WARNING_TRACING_DISABLED = "You added a ApmNetworkInstrumentation to your %s, " +
            "but you did not enable the TracingFeature. " +
            "Your requests won't be traced."

        const val WARNING_DEFAULT_TRACER =
            "You added a ApmNetworkInstrumentation to your %s instrumentation, " +
                "but you didn't register any OpenObserveTracer. " +
                "We automatically created a local tracer for you."
    }
}
