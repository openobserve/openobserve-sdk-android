/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.trace.api

import com.openobserve.trace.api.DDTraceId
import com.openobserve.trace.api.IdGenerationStrategy

class TestIdGenerationStrategy(
    val traceIds: List<Long>? = null,
    val spanIds: List<Long>? = null
) : IdGenerationStrategy(false) {

    private var index: Int = -1
    private val fallbackDelegate = fromName("SECURE_RANDOM")

    override fun generateTraceId(): DDTraceId = if (traceIds == null || traceIds.isEmpty()) {
        fallbackDelegate.generateTraceId()
    } else {
        DDTraceId.from(traceIds[++index % traceIds.size])
    }

    override fun generateSpanId() = if (spanIds == null || spanIds.isEmpty()) {
        fallbackDelegate.generateSpanId()
    } else {
        spanIds[++index % spanIds.size]
    }

    override fun getNonZeroPositiveLong(): Long = 1
}
