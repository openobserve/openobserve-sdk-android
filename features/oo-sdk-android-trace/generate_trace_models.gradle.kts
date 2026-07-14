/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

import com.openobserve.gradle.utils.createJsonModelsGenerationTask
import java.nio.file.Paths

createJsonModelsGenerationTask("generateTraceModelsFromJson") {
    inputDirPath = "src/main/json/trace"
    ignoredFiles = listOf(
        "_common-schema.json"
    )
    targetPackageName = "com.openobserve.android.trace.model"
    extraInputWatchDir = project.layout.projectDirectory.dir(
        Paths.get("../oo-sdk-android-rum/src/main/json/rum").toString()
    )
}
