/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.trace.api

import com.openobserve.android.trace.GlobalOpenObserveTracer
import com.openobserve.android.trace.api.span.OpenObserveSpan
import com.openobserve.android.trace.api.span.OpenObserveSpanContext
import com.openobserve.android.trace.api.trace.OpenObserveTraceId
import com.openobserve.android.trace.api.tracer.OpenObserveTracer
import com.openobserve.android.trace.api.tracer.OpenObserveTracerBuilder
import com.openobserve.android.trace.internal.OpenObservePropagationHelper
import com.openobserve.android.trace.internal.OpenObserveSpanAdapter
import com.openobserve.android.trace.internal.OpenObserveSpanContextAdapter
import com.openobserve.android.trace.internal.OpenObserveTraceIdAdapter
import com.openobserve.android.trace.internal.OpenObserveTracerAdapter
import com.openobserve.android.trace.internal.OpenObserveTracerBuilderAdapter
import com.openobserve.android.trace.internal._TraceInternalProxy
import com.openobserve.trace.api.DDTraceId
import com.openobserve.trace.core.CoreTracer
import com.openobserve.trace.core.DDSpanContext

val OpenObserveTracer.partialFlushMinSpans: Int?
    get() = coreTracer?.partialFlushMinSpans

val OpenObserveSpanContext.resourceName: String?
    get() = ddSpanContext?.resourceName?.toString()

val OpenObserveSpanContext.serviceName: String?
    get() = ddSpanContext?.serviceName?.toString()

val OpenObserveTraceId.Companion.ZERO: OpenObserveTraceId
    get() = OpenObserveTraceIdAdapter(DDTraceId.ZERO)

fun OpenObserveTraceId.Companion.from(traceId: Long): OpenObserveTraceId {
    return OpenObserveTraceIdAdapter(DDTraceId.from(traceId))
}

fun OpenObserveTraceId.Companion.from(traceId: String): OpenObserveTraceId {
    return OpenObserveTraceIdAdapter(DDTraceId.from(traceId))
}

fun OpenObserveSpan.forceSamplingDecision() {
    (this as OpenObserveSpanAdapter).delegate.forceSamplingDecision()
}

fun _TraceInternalProxy.setTracingAdapterBuilderMock(mock: OpenObserveTracerBuilder?) {
    testBuilderProvider = mock
}

fun _TraceInternalProxy.clear() {
    setTracingAdapterBuilderMock(null)
}

fun _TraceInternalProxy.withMockPropagationHelper(
    mockHelper: OpenObservePropagationHelper,
    block: _TraceInternalProxy.() -> Unit
) {
    val helper = propagationHelper
    try {
        propagationHelper = mockHelper
        block()
    } finally {
        propagationHelper = helper
    }
}

fun OpenObserveTracerBuilder.setTestIdGenerationStrategy(strategy: TestIdGenerationStrategy) = apply {
    (this as? OpenObserveTracerBuilderAdapter)?.setCustomIdGenerationStrategy(strategy)
}

fun GlobalOpenObserveTracer.replace(
    builder: OpenObserveTracerBuilder
): Boolean {
    clear()
    return registerIfAbsent(builder.build())
}

private val OpenObserveSpanContext.ddSpanContext: DDSpanContext?
    get() {
        val spanContextAdapter = this as? OpenObserveSpanContextAdapter
        return spanContextAdapter?.delegate as? DDSpanContext
    }

private val OpenObserveTracer.coreTracer: CoreTracer?
    get() {
        val tracerAdapter = this as? OpenObserveTracerAdapter
        return tracerAdapter?.delegate as? CoreTracer
    }
