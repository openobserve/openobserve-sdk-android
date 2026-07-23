/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.sqldelight.internal

import com.openobserve.android.trace.GlobalOpenObserveTracer
import com.openobserve.android.trace.api.span.OpenObserveSpan

@Suppress("ThrowingInternalException", "TooGenericExceptionCaught")
internal inline fun <T : Any?> withinSpan(
    operationName: String,
    parentSpan: OpenObserveSpan? = null,
    block: OpenObserveSpan.() -> T
): T {
    val tracer = GlobalOpenObserveTracer.get()

    val span = tracer.buildSpan(operationName)
        .withParentSpan(parentSpan)
        .start()

    val scope = tracer.activateSpan(span)

    return try {
        span.block()
    } catch (e: Throwable) {
        span.addThrowable(e)
        throw e
    } finally {
        span.finish()
        scope?.close()
    }
}
