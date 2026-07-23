/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

import com.openobserve.gradle.config.AndroidConfig
import com.openobserve.gradle.config.androidLibraryConfig
import com.openobserve.gradle.config.dependencyUpdateConfig
import com.openobserve.gradle.config.java11
import com.openobserve.gradle.config.junitConfig
import com.openobserve.gradle.config.kotlinConfig
import com.openobserve.gradle.config.publishingConfig

plugins {
    id("com.android.library")
    kotlin("android")
    id("com.github.ben-manes.versions")

    `maven-publish`
    signing
}

android {
    defaultConfig {
        compileSdk = AndroidConfig.TARGET_SDK
        minSdk = AndroidConfig.MIN_SDK
    }
    namespace = "com.openobserve.tools.benchmark"
    compileOptions {
        java11()
    }
}

dependencies {
    implementation(project(":o2-sdk-android-core"))
    implementation(project(":o2-sdk-android-internal"))
    implementation(libs.kotlin)
    implementation(libs.okHttp)
    implementation(libs.openTelemetryApi)
    implementation(libs.openTelemetrySdk)
    implementation(libs.gson)
    testImplementation(project(":tools:unit")) {
        attributes {
            attribute(
                com.android.build.api.attributes.ProductFlavorAttr.of("platform"),
                objects.named("jvm")
            )
        }
    }

    testImplementation(libs.bundles.jUnit5)
    testImplementation(libs.bundles.testTools)
}

apply(from = "generate_trace_models.gradle.kts")

kotlinConfig()
junitConfig()
dependencyUpdateConfig()
androidLibraryConfig()
publishingConfig(
    projectDescription = "An internal benchmarking tool to measure the overhead of OpenObserve SDK",
    customArtifactId = "o2-sdk-android-benchmark-internal"
)
