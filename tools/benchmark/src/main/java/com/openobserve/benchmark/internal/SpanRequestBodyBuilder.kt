/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark.internal

import com.openobserve.benchmark.internal.model.BenchmarkContext
import com.openobserve.benchmark.internal.model.SpanEvent

internal class SpanRequestBodyBuilder(private val context: BenchmarkContext) : RequestBodyBuilder<SpanEvent> {

    private val serializer = SpanEventSerializer()

    override fun build(data: List<SpanEvent>): String {
        return serializer.serialize(
            context.env,
            data
        )
    }
}
