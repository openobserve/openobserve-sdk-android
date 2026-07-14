/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark.profiler

import com.openobserve.android.internal.profiler.BenchmarkSpanBuilder
import com.openobserve.android.internal.profiler.BenchmarkTracer
import io.opentelemetry.api.trace.Tracer
import io.opentelemetry.context.Context

/**
 * Implementation of [BenchmarkTracer].
 */
class DDBenchmarkTracer(private val tracer: Tracer) : BenchmarkTracer {

    override fun spanBuilder(
        spanName: String,
        additionalProperties: Map<String, String>
    ): BenchmarkSpanBuilder {
        return DDBenchmarkSpanBuilder(
            tracer
                .spanBuilder(spanName)
                .apply {
                    additionalProperties.forEach {
                        this.setAttribute(it.key, it.value)
                    }
                }
                .setParent(Context.current())
        )
    }
}
