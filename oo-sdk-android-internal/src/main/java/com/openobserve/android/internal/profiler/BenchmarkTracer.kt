/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.internal.profiler

import com.openobserve.tools.annotation.NoOpImplementation

/**
 * Interface of benchmark tracer to be implemented to provide [BenchmarkSpan].
 * This should only be used by internal benchmarking.
 */
@NoOpImplementation
interface BenchmarkTracer {

    /**
     * Returns a new [BenchmarkSpanBuilder].
     *
     * @param spanName The name of the returned span.
     * @param additionalProperties Additional properties for this span.
     * @return a new [BenchmarkSpanBuilder].
     */
    fun spanBuilder(
        spanName: String,
        additionalProperties: Map<String, String> = emptyMap()
    ): BenchmarkSpanBuilder
}
