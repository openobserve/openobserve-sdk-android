/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.sdk.integration.trace

import com.openobserve.android.trace.api.span.OpenObserveSpan
import com.openobserve.android.trace.internal._TraceInternalProxy

/**
 * Returns the span's least significant trace id in hex format (the last 64 bits from the 128 bits trace id)
 */
fun OpenObserveSpan.leastSignificant64BitsTraceId(): String {
    return traceId.toHexString().padStart(32, '0').takeLast(16)
}

/**
 * Returns the span's most significant trace id in hex format (the first 64 bits from the 128 bits trace id)
 */
fun OpenObserveSpan.mostSignificant64BitsTraceId(): String {
    return traceId.toHexString().padStart(32, '0').take(16)
}

/**
 * Returns the span's spanId in hex format.
 */
fun OpenObserveSpan.spanIdAsHexString(): String {
    return _TraceInternalProxy.spanIdConverter.toHexStringPadded(context().spanId)
}
