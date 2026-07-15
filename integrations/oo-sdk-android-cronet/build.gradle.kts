/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

import com.openobserve.gradle.config.androidLibraryConfig
import com.openobserve.gradle.config.dependencyUpdateConfig
import com.openobserve.gradle.config.detektCustomConfig
import com.openobserve.gradle.config.javadocConfig
import com.openobserve.gradle.config.junitConfig
import com.openobserve.gradle.config.kotlinConfig
import com.openobserve.gradle.config.publishingConfig
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    // Build
    id("com.android.library")
    kotlin("android")

    // Publish
    `maven-publish`
    signing
    id("org.jetbrains.dokka-javadoc")

    // Analysis tools
    id("com.github.ben-manes.versions")

    // Tests
    id("de.mobilej.unmock")
    id("org.jetbrains.kotlinx.kover")

    // Internal Generation
    id("apiSurface")
    id("transitiveDependencies")
    id("verificationXml")
    id("binary-compatibility-validator")
}

android {
    namespace = "com.openobserve.android.cronet"
    lint {
        // Cronet library has experimental annotations that AndroidX lint checker
        // cannot properly parse, causing "Failed to extract attribute 'level'" error
        disable += "UnsafeOptInUsageError"
    }
}

dependencies {
    api(libs.cronetApi)
    implementation(libs.kotlin)
    implementation(libs.androidXAnnotation)

    implementation(project(":oo-sdk-android-internal"))
    implementation(project(":features:oo-sdk-android-rum"))
    implementation(project(":features:oo-sdk-android-trace"))

    unmock(libs.robolectric)
    // Trying to add most recent lib version in order to test the instrumentation, see CronetApiInstrumentationTest
    testImplementation("${libs.cronetApi.get().module}:+")
    testImplementation(testFixtures(project(":oo-sdk-android-core")))
    testImplementation(testFixtures(project(":oo-sdk-android-internal")))
    testImplementation(testFixtures(project(":features:oo-sdk-android-rum")))
    testImplementation(testFixtures(project(":features:oo-sdk-android-trace")))

    testImplementation(libs.elmyrJUnit4)
    testImplementation(libs.bundles.jUnit5)
    testImplementation(libs.bundles.testTools)
    testImplementation(project(":tools:unit")) {
        attributes {
            attribute(
                com.android.build.api.attributes.ProductFlavorAttr.of("platform"),
                objects.named("jvm")
            )
        }
    }
}

unMock {
    keepStartingWith("org.json")
}

kotlinConfig(jvmBytecodeTarget = JvmTarget.JVM_11)
androidLibraryConfig()
junitConfig()
javadocConfig()
dependencyUpdateConfig()
publishingConfig(
    "A Cronet monitoring integration to use with the OpenObserve monitoring library for Android applications."
)
detektCustomConfig()
