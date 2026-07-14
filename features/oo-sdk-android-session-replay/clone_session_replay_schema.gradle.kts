/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
import com.openobserve.gradle.utils.cloneRumEventsFormat
import com.openobserve.gradle.utils.createRumSchemaCloneTask

createRumSchemaCloneTask("cloneSessionReplayRootSchemas") {
    cloneRumEventsFormat(
        project = project,
        subFolder = "schemas/",
        destinationFolder = "src/main/json/schemas",
        excludedPrefixes = listOf(
            "profiling",
            "session-replay/",
            "rum",
            "mobile",
            "telemetry",
            "session-replay-schema",
            "session-replay-browser-schema"
        )
    )
}

createRumSchemaCloneTask("cloneSessionReplayMobileSchemas") {
    cloneRumEventsFormat(
        project = project,
        subFolder = "schemas/session-replay/mobile",
        destinationFolder = "src/main/json/schemas/session-replay/mobile"
    )
}

createRumSchemaCloneTask("cloneSessionReplayCommonSchemas") {
    cloneRumEventsFormat(
        project = project,
        subFolder = "schemas/session-replay/common",
        destinationFolder = "src/main/json/schemas/session-replay/common"
    )
}

tasks.register("cloneSessionReplaySchemas") {
    dependsOn("cloneSessionReplayRootSchemas")
    dependsOn("cloneSessionReplayMobileSchemas")
    dependsOn("cloneSessionReplayCommonSchemas")
}
