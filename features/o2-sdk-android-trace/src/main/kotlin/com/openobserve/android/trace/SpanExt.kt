/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.trace

import com.openobserve.android.trace.api.span.OpenObserveSpan

/**
 * Wraps the provided lambda within a [OpenObserveSpan].
 * @param T the type returned by the lambda
 * @param operationName the name of the [OpenObserveSpan] created around the lambda
 * @param parentSpan the parent [OpenObserveSpan] (default is `null`)
 * @param activate whether the created [OpenObserveSpan] should be made active for the current thread
 * (default is `true`)
 * @param block the lambda function traced by this newly created [OpenObserveSpan]
 *
 */
@SuppressWarnings("TooGenericExceptionCaught")
inline fun <T : Any?> withinSpan(
    operationName: String,
    parentSpan: OpenObserveSpan? = null,
    activate: Boolean = true,
    block: OpenObserveSpan.() -> T
): T {
    val tracer = GlobalOpenObserveTracer.get()

    val span = tracer.buildSpan(operationName)
        .withParentSpan(parentSpan)
        .start()

    val scope = if (activate) tracer.activateSpan(span) else null

    return try {
        span.block()
    } catch (e: Throwable) {
        span.logThrowable(e)
        throw e
    } finally {
        span.finish()
        scope?.close()
    }
}
