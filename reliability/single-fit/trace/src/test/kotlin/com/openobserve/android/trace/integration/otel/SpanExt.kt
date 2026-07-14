/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.trace.integration.otel

import com.openobserve.android.trace.api.span.OpenObserveSpan
import com.openobserve.tools.unit.getFieldValue
import io.opentelemetry.api.trace.Span

internal fun Span.leastSignificant64BitsTraceIdAsHex(): String {
    return spanContext.traceId.takeLast(16)
}

internal fun Span.mostSignificant64BitsTraceIdAsHex(): String {
    return spanContext.traceId.take(16)
}

internal fun Span.spanIdAsHex(): String {
    return spanContext.spanId
}

internal fun Span.expectedSpanName(): String {
    val ooSpan: OpenObserveSpan = this.getFieldValue("delegate")
    return ooSpan.operationName
}
