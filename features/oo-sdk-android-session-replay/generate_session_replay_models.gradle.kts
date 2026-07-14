/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

import com.openobserve.gradle.utils.createJsonModelsGenerationTask

val generateSessionReplayModelsTaskName = "generateSessionReplayModels"

createJsonModelsGenerationTask(generateSessionReplayModelsTaskName) {
    inputDirPath = "src/main/json/schemas"
    targetPackageName = "com.openobserve.android.sessionreplay.model"
}
