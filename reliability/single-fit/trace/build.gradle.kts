/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

import com.openobserve.gradle.config.androidLibraryConfig
import com.openobserve.gradle.config.dependencyUpdateConfig
import com.openobserve.gradle.config.junitConfig
import com.openobserve.gradle.config.kotlinConfig
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    // Build
    id("com.android.library")
    kotlin("android")
    id("com.google.devtools.ksp")

    // Analysis tools
    id("com.github.ben-manes.versions")

    // Tests
    id("de.mobilej.unmock")
}

android {
    namespace = "com.openobserve.android.trace.integration"

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
}

dependencies {
    implementation(project(":oo-sdk-android-core"))
    implementation(project(":features:oo-sdk-android-trace"))
    implementation(project(":features:oo-sdk-android-trace-otel"))
    implementation(libs.kotlin)

    // Desugaring SDK
    coreLibraryDesugaring(libs.androidDesugaringSdk)

    // Testing
    testImplementation(project(":tools:unit")) {
        attributes {
            attribute(
                com.android.build.api.attributes.ProductFlavorAttr.of("platform"),
                objects.named("jvm")
            )
        }
    }
    testImplementation(project(":reliability:stub-core"))
    testImplementation(project(":oo-sdk-android-internal"))
    testImplementation(project(":features:oo-sdk-android-trace-internal"))
    testImplementation(testFixtures(project(":oo-sdk-android-core")))
    testImplementation(testFixtures(project(":features:oo-sdk-android-trace")))
    testImplementation(libs.bundles.jUnit5)
    testImplementation(libs.bundles.testTools)
    testImplementation(libs.okHttp)
    testImplementation(libs.gson)
    unmock(libs.robolectric)
}

unMock {
    keep("android.util.Singleton")
    keep("com.android.internal.util.FastPrintWriter")
    keep("dalvik.system.BlockGuard")
    keep("dalvik.system.CloseGuard")
    keepStartingWith("android.os")
    keepStartingWith("org.json")
}

androidLibraryConfig()
kotlinConfig(jvmBytecodeTarget = JvmTarget.JVM_11)
junitConfig()
dependencyUpdateConfig()
