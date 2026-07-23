/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */
package com.openobserve.android.trace.internal

import com.openobserve.android.trace.api.span.OpenObserveSpanContext
import com.openobserve.android.trace.api.trace.OpenObserveTraceId
import com.openobserve.trace.api.sampling.SamplingMechanism
import com.openobserve.trace.bootstrap.instrumentation.api.AgentSpan
import com.openobserve.trace.core.DDSpanContext
import com.openobserve.trace.core.PendingTrace

internal class OpenObserveSpanContextAdapter(internal val delegate: AgentSpan.Context) : OpenObserveSpanContext {
    override val spanId: Long get() = delegate.spanId
    override val samplingPriority: Int get() = delegate.traceSamplingPriority
    override val tags: Map<String, Any?> get() = ddSpanContext?.tags.orEmpty()
    override val traceId: OpenObserveTraceId get() = OpenObserveTraceIdAdapter(delegate.traceId)

    private val ddSpanContext: DDSpanContext?
        get() = delegate as? DDSpanContext

    override fun setSamplingPriority(samplingPriority: Int): Boolean {
        return ddSpanContext?.setSamplingPriority(samplingPriority, SamplingMechanism.DEFAULT.toInt()) ?: false
    }

    override fun setMetric(key: CharSequence?, value: Double) {
        ddSpanContext?.setMetric(key, value)
    }

    internal fun setTracingSamplingPriorityIfNecessary() {
        (delegate.trace as? PendingTrace)?.setSamplingPriorityIfNecessary()
    }
}
