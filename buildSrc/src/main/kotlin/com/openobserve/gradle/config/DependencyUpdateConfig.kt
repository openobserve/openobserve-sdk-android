/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.gradle.config

import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.gradle.api.Project

fun Project.dependencyUpdateConfig() {
    taskConfig<DependencyUpdatesTask> {
        revision = "release"
    }
}
