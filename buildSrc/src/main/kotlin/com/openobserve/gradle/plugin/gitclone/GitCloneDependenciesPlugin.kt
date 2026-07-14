/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.gradle.plugin.gitclone

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register

class GitCloneDependenciesPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val extension = target.extensions
            .create<GitCloneDependenciesExtension>(EXT_NAME)

        target.tasks
            .register<GitCloneDependenciesTask>(TASK_NAME) {
                this.projectDirPath.set(project.layout.projectDirectory.asFile.path)
                this.extension = extension
            }
    }

    companion object {
        const val EXT_NAME = "cloneDependencies"

        const val TASK_NAME = "cloneDependencies"
    }
}
