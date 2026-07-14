/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.trace.internal

import com.openobserve.android.api.feature.FeatureSdkCore
import com.openobserve.android.trace.api.scope.OpenObserveScopeListener
import com.openobserve.android.trace.api.tracer.OpenObserveTracer

internal class TracePropagationScopeListener(
    private val sdkCore: FeatureSdkCore,
    private val ooTracer: OpenObserveTracer
) : OpenObserveScopeListener {
    override fun afterScopeActivated() {
        val activeSpanContext = ooTracer.activeSpan()?.context()
        if (activeSpanContext != null) {
            val activeSpanId = activeSpanContext.spanId.toString()
            val activeTraceId = activeSpanContext.traceId.toHexString()
            sdkCore.addActiveTraceToContext(activeTraceId, activeSpanId)
        }
    }

    override fun afterScopeClosed() {
        sdkCore.removeActiveTraceFromContext()
    }
}
