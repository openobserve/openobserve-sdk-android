/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.trace.api.span

import com.openobserve.android.trace.api.trace.OpenObserveTraceId
import com.openobserve.tools.annotation.NoOpImplementation

/**
 * [OpenObserveSpanContext] represents span state that must propagate to descendant spans and across process boundaries.
 */
@NoOpImplementation
interface OpenObserveSpanContext {
    /**
     * Represents the unique identifier for a OpenObserve trace.
     */
    val traceId: OpenObserveTraceId

    /**
     * Represents the unique identifier for a OpenObserve span.
     */
    val spanId: Long

    /**
     * Represents the sampling priority value for a span.
     */
    val samplingPriority: Int

    /**
     * Represents a collection of tags associated with the span.
     */
    val tags: Map<String, Any?>

    /**
     * Sets the sampling priority for the span.
     *
     * @param samplingPriority The sampling priority value to be set for the span.
     * @return True if the sampling priority was successfully set, false otherwise.
     */
    fun setSamplingPriority(samplingPriority: Int): Boolean

    /**
     * Sets a numerical metric associated with the span.
     *
     * @param key The name of the metric to be set.
     * @param value The numerical value of the metric to be associated with the specified key.
     */
    fun setMetric(key: CharSequence?, value: Double)
}
