/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.telemetry.internal

internal enum class TelemetryType {
    DEBUG,
    ERROR,
    CONFIGURATION,
    INTERCEPTOR_SETUP,
    API_USAGE,
    METRIC,
    RESOURCE_HEADERS_SETUP
}
