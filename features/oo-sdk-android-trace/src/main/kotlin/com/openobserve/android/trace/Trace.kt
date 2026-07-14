/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.trace

import com.openobserve.android.OpenObserve
import com.openobserve.android.api.SdkCore
import com.openobserve.android.api.feature.FeatureSdkCore
import com.openobserve.android.trace.internal.TracingFeature

/**
 * An entry point to OpenObserve Traces feature.
 */
object Trace {

    /**
     * Enables a Traces feature based on the configuration provided.
     *
     * @param traceConfiguration Configuration to use for the feature.
     * @param sdkCore SDK instance to register feature in. If not provided, default SDK instance
     * will be used.
     */
    @JvmOverloads
    @JvmStatic
    fun enable(traceConfiguration: TraceConfiguration, sdkCore: SdkCore = OpenObserve.getInstance()) {
        val tracingFeature = TracingFeature(
            sdkCore = sdkCore as FeatureSdkCore,
            customEndpointUrl = traceConfiguration.customEndpointUrl,
            spanEventMapper = traceConfiguration.eventMapper,
            networkInfoEnabled = traceConfiguration.networkInfoEnabled
        )

        sdkCore.registerFeature(tracingFeature)
    }
}
