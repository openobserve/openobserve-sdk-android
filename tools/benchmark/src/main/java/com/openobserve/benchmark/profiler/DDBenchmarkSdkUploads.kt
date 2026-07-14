/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark.profiler

import com.openobserve.android.internal.profiler.BenchmarkMeter
import com.openobserve.android.internal.profiler.BenchmarkSdkUploads
import io.opentelemetry.api.GlobalOpenTelemetry

/**
 * Implementation of [BenchmarkSdkUploads] for internal benchmarking.
 */
class DDBenchmarkSdkUploads : BenchmarkSdkUploads {
    override fun getMeter(operation: String): BenchmarkMeter {
        return DDBenchmarkMeter(GlobalOpenTelemetry.get().getMeter(operation))
    }
}
