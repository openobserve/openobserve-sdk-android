/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

import com.openobserve.gradle.utils.createJsonModelsGenerationTask

createJsonModelsGenerationTask("generateTelemetryModelsFromJson") {
    inputDirPath = "src/main/json/telemetry"
    targetPackageName = "com.openobserve.android.telemetry.model"
    ignoredFiles = listOf(
        "_common-schema.json"
    )
    inputNameMapping = mapOf(
        "debug-schema.json" to "TelemetryDebugEvent",
        "error-schema.json" to "TelemetryErrorEvent"
    )
}
