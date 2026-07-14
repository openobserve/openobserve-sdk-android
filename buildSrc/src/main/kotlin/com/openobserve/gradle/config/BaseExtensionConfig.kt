/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.gradle.config

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.withType

inline fun <reified T : Any> Project.extensionConfig(
    crossinline configure: T.() -> Unit
) {
    val ext: T? = extensions.findByType(T::class)
    ext?.configure()
}

inline fun <reified T : Task> Project.taskConfig(
    crossinline configure: T.() -> Unit
) {
    tasks.withType<T>().configureEach { configure() }
}
