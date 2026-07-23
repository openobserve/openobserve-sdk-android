/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */
package com.openobserve.android.trace.internal

import com.openobserve.android.lint.InternalApi
import com.openobserve.android.trace.api.trace.OpenObserveTraceId
import com.openobserve.trace.api.DDTraceId

/**
 * For OpenObserve internal use only.
 *
 * Converts a hexadecimal string representation of a trace ID into a [OpenObserveTraceId] instance.
 *
 * @param traceId The hexadecimal string representation of the trace ID to be converted.
 * @return The corresponding [OpenObserveTraceId] instance.
 */
@InternalApi
fun OpenObserveTraceId.Companion.fromHex(traceId: String): OpenObserveTraceId {
    return OpenObserveTraceIdAdapter(DDTraceId.fromHex(traceId))
}
