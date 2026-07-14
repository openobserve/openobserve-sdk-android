/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.telemetry.internal

import com.openobserve.android.internal.telemetry.TracingHeaderType
import com.openobserve.android.internal.telemetry.TracingHeaderTypesSet
import com.openobserve.android.telemetry.model.TelemetryConfigurationEvent

internal fun TracingHeaderTypesSet.toSelectedTracingPropagators() = types.map { it.toSelectedTracingPropagator() }

private fun TracingHeaderType.toSelectedTracingPropagator(): TelemetryConfigurationEvent.SelectedTracingPropagator {
    return when (this) {
        TracingHeaderType.OPENOBSERVE ->
            TelemetryConfigurationEvent.SelectedTracingPropagator.OPENOBSERVE

        TracingHeaderType.B3 ->
            TelemetryConfigurationEvent.SelectedTracingPropagator.B3

        TracingHeaderType.B3MULTI ->
            TelemetryConfigurationEvent.SelectedTracingPropagator.B3MULTI

        TracingHeaderType.TRACECONTEXT ->
            TelemetryConfigurationEvent.SelectedTracingPropagator.TRACECONTEXT
    }
}
