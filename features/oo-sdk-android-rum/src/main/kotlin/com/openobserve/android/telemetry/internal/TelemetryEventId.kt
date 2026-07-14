/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.telemetry.internal

import com.openobserve.android.internal.telemetry.InternalTelemetryEvent

internal data class TelemetryEventId(
    val type: TelemetryType,
    val message: String,
    val kind: String?
)

internal val InternalTelemetryEvent.identity: TelemetryEventId
    get() {
        return when (this) {
            is InternalTelemetryEvent.Log.Error -> TelemetryEventId(type(), message, resolveKind())
            is InternalTelemetryEvent.Log.Debug -> TelemetryEventId(type(), message, null)
            else -> TelemetryEventId(type(), "", null)
        }
    }

internal fun InternalTelemetryEvent.type(): TelemetryType {
    return when (this) {
        is InternalTelemetryEvent.Log.Debug -> TelemetryType.DEBUG
        is InternalTelemetryEvent.Log.Error -> TelemetryType.ERROR
        is InternalTelemetryEvent.Configuration -> TelemetryType.CONFIGURATION
        is InternalTelemetryEvent.Metric -> TelemetryType.METRIC
        is InternalTelemetryEvent.ApiUsage -> TelemetryType.API_USAGE
        is InternalTelemetryEvent.InterceptorInstantiated -> TelemetryType.INTERCEPTOR_SETUP
        is InternalTelemetryEvent.ResourceHeadersTrackingConfigured -> TelemetryType.RESOURCE_HEADERS_SETUP
    }
}
