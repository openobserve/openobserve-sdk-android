/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.okhttp.internal.trace

import com.openobserve.android.trace.TracingHeaderType
import com.openobserve.android.internal.telemetry.TracingHeaderType as TelemetryTracingHeaderType

internal fun TracingHeaderType.toTelemetryTracingHeaderType(): TelemetryTracingHeaderType {
    return when (this) {
        TracingHeaderType.OPENOBSERVE -> TelemetryTracingHeaderType.OPENOBSERVE
        TracingHeaderType.B3 -> TelemetryTracingHeaderType.B3
        TracingHeaderType.B3MULTI -> TelemetryTracingHeaderType.B3MULTI
        TracingHeaderType.TRACECONTEXT -> TelemetryTracingHeaderType.TRACECONTEXT
    }
}
