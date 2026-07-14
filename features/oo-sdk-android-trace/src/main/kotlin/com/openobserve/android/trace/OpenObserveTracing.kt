/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.trace

import com.openobserve.android.OpenObserve
import com.openobserve.android.api.InternalLogger
import com.openobserve.android.api.SdkCore
import com.openobserve.android.api.feature.Feature
import com.openobserve.android.api.feature.FeatureSdkCore
import com.openobserve.android.trace.api.tracer.OpenObserveTracerBuilder
import com.openobserve.android.trace.api.tracer.NoOpOpenObserveTracerBuilder
import com.openobserve.android.trace.internal.OpenObserveSpanWriterWrapper
import com.openobserve.android.trace.internal.OpenObserveTracerBuilderAdapter
import com.openobserve.android.trace.internal._TraceInternalProxy
import com.openobserve.trace.common.writer.NoOpWriter
import com.openobserve.trace.core.CoreTracer

/**
 * Object responsible for providing tracing capabilities in the OpenObserve SDK.
 */
@SuppressWarnings("UndocumentedPublicFunction")
object OpenObserveTracing {
    /**
     * Creates and returns a new instance of [OpenObserveTracerBuilder]. This method initializes and configures the
     * tracer builder based on the provided SDK core instance and its setup. If certain required configurations
     * are missing, a no-operation tracer builder is returned instead.
     *
     * @param sdkCore An instance of [SdkCore], which provides configuration and features needed for initializing
     * the tracer builder.
     * @return A configured instance of [OpenObserveTracerBuilder], or a no-operation implementation if the necessary
     * configurations or features are unavailable.
     */
    @JvmStatic
    fun newTracerBuilder(sdkCore: SdkCore = OpenObserve.getInstance()): OpenObserveTracerBuilder = when {
        _TraceInternalProxy.testBuilderProvider != null -> {
            _TraceInternalProxy.testBuilderProvider as OpenObserveTracerBuilder
        }
        sdkCore !is FeatureSdkCore -> {
            NoOpOpenObserveTracerBuilder()
        }
        else -> {
            val internalLogger = sdkCore.internalLogger
            val tracingFeature = sdkCore.getFeature(Feature.TRACING_FEATURE_NAME)?.unwrap<Feature>()
            val internalCoreWriterProvider = tracingFeature as? InternalCoreWriterProvider
            val writer = (internalCoreWriterProvider?.getCoreTracerWriter() as? OpenObserveSpanWriterWrapper)?.delegate

            when {
                null == tracingFeature -> internalLogger.log(
                    InternalLogger.Level.ERROR,
                    InternalLogger.Target.USER,
                    { ErrorMessages.TRACING_NOT_ENABLED_ERROR_MESSAGE }
                )

                null == internalCoreWriterProvider -> internalLogger.log(
                    InternalLogger.Level.ERROR,
                    InternalLogger.Target.MAINTAINER,
                    { ErrorMessages.WRITER_PROVIDER_INTERFACE_NOT_IMPLEMENTED_ERROR_MESSAGE }
                )

                null == writer -> internalLogger.log(
                    InternalLogger.Level.ERROR,
                    InternalLogger.Target.USER,
                    {
                        ErrorMessages.buildWrongWrapperMessage(
                            internalCoreWriterProvider.getCoreTracerWriter().javaClass
                        )
                    }
                )

                sdkCore.service.isEmpty() -> internalLogger.log(
                    InternalLogger.Level.ERROR,
                    InternalLogger.Target.USER,
                    { ErrorMessages.DEFAULT_SERVICE_NAME_IS_MISSING_ERROR_MESSAGE }
                )
            }

            OpenObserveTracerBuilderAdapter(
                sdkCore = sdkCore,
                serviceName = sdkCore.service,
                delegate = CoreTracer.CoreTracerBuilder(sdkCore.internalLogger).writer(writer ?: NoOpWriter())
            )
        }
    }

    internal object ErrorMessages {
        const val TRACING_NOT_ENABLED_ERROR_MESSAGE: String =
            "You're trying to create an OpenObserveTracer instance, " +
                "but either the SDK was not initialized or the Tracing feature was " +
                "not registered. No tracing data will be sent."

        const val WRITER_PROVIDER_INTERFACE_NOT_IMPLEMENTED_ERROR_MESSAGE: String =
            "The Tracing feature is not implementing the InternalCoreWriterProvider interface." +
                " No tracing data will be sent."

        const val DEFAULT_SERVICE_NAME_IS_MISSING_ERROR_MESSAGE: String =
            "Default service name is missing during OpenObserveTracerBuilder creation, did you initialize SDK?"

        fun buildWrongWrapperMessage(cls: Class<*>?): String {
            return "You're trying to create an OpenObserveTracer instance, " +
                "but provided ${cls?.canonicalName} writer wrapper is not supported."
        }
    }
}
