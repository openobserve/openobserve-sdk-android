/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.compose.internal

import com.openobserve.android.OpenObserve
import com.openobserve.android.api.InternalLogger
import com.openobserve.android.api.SdkCore
import com.openobserve.android.api.feature.FeatureSdkCore

internal fun sendTelemetry(
    autoInstrumented: Boolean = false,
    instrumentationType: InstrumentationType,
    supportLibrary: SupportLibrary? = null,
    sdkCore: SdkCore = OpenObserve.getInstance()
) {
    val message = "$OPENOBSERVE_SEMANTICS_TELEMETRY_LOG: ${instrumentationType.value}"
    val attributes = mapOf(
        KEY_COMPOSE_INSTRUMENTATION to mapOf(
            KEY_ENABLED to autoInstrumented,
            KEY_INSTRUMENTATION_TYPE to instrumentationType,
            KEY_SUPPORT_LIBRARY to supportLibrary
        )
    )
    (sdkCore as? FeatureSdkCore)?.internalLogger?.log(
        level = InternalLogger.Level.INFO,
        target = InternalLogger.Target.TELEMETRY,
        messageBuilder = { message },
        onlyOnce = true,
        additionalProperties = attributes
    )
}

internal enum class InstrumentationType(val value: String) {
    Semantics("Semantics"),
    ViewTracking("ViewTracking")
}

internal enum class SupportLibrary(val value: String) {
    Navigation("Navigation"),
    Navigation3("Navigation3")
}

private const val KEY_COMPOSE_INSTRUMENTATION = "compose_instrumentation"

private const val KEY_ENABLED = "enabled"

private const val KEY_SUPPORT_LIBRARY = "support_library"

private const val KEY_INSTRUMENTATION_TYPE = "instrumentation_type"

private const val OPENOBSERVE_SEMANTICS_TELEMETRY_LOG =
    "OpenObserve Compose Integration Telemetry"
