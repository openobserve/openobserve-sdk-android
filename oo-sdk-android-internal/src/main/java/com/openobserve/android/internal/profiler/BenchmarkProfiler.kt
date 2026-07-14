/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.internal.profiler

import com.openobserve.tools.annotation.NoOpImplementation

/**
 * Interface of benchmark profiler to be implemented to provide [BenchmarkTracer].
 * This should only be used by internal benchmarking.
 */
@NoOpImplementation
interface BenchmarkProfiler {

    /**
     * Returns a [BenchmarkTracer].
     */
    fun getTracer(operation: String): BenchmarkTracer
}
