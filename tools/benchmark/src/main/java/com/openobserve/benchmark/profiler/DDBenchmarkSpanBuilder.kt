/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark.profiler

import com.openobserve.android.internal.profiler.BenchmarkSpan
import com.openobserve.android.internal.profiler.BenchmarkSpanBuilder
import io.opentelemetry.api.trace.SpanBuilder

/**
 * Implementation of [BenchmarkSpanBuilder].
 */
class DDBenchmarkSpanBuilder(
    private val spanBuilder: SpanBuilder
) : BenchmarkSpanBuilder {

    override fun startSpan(): BenchmarkSpan {
        val span = spanBuilder.startSpan()
        return DDBenchmarkSpan(span, span.makeCurrent())
    }
}
