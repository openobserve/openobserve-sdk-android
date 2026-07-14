/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark.profiler

import com.openobserve.android.internal.profiler.BenchmarkProfiler
import com.openobserve.android.internal.profiler.BenchmarkTracer
import io.opentelemetry.api.GlobalOpenTelemetry

/**
 * Implementation of [BenchmarkProfiler].
 */
class DDBenchmarkProfiler : BenchmarkProfiler {

    override fun getTracer(operation: String): BenchmarkTracer {
        return DDBenchmarkTracer(GlobalOpenTelemetry.get().getTracer(operation))
    }
}
