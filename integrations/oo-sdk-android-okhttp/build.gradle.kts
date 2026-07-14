/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

@file:Suppress("StringLiteralDuplication")

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
    id("com.google.devtools.ksp")

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
    namespace = "com.openobserve.android.okhttp"
}

dependencies {
    implementation(libs.kotlin)
    implementation(libs.okHttp)
    implementation(libs.androidXAnnotation)
    implementation(project(":oo-sdk-android-internal"))
    implementation(project(":features:oo-sdk-android-rum"))
    // TODO RUM-15626: Move TraceContextInjection to oo-sdk-android-trace-api module,
    //  then replace api(oo-sdk-android-trace) with api(oo-sdk-android-trace-api).
    api(project(":features:oo-sdk-android-trace"))
    // Generate NoOp implementations
    ksp(project(":tools:noopfactory"))

    testImplementation(project(":tools:unit")) {
        attributes {
            attribute(
                com.android.build.api.attributes.ProductFlavorAttr.of("platform"),
                objects.named("jvm")
            )
        }
    }
    testImplementation(testFixtures(project(":oo-sdk-android-core")))
    testImplementation(testFixtures(project(":oo-sdk-android-internal")))
    testImplementation(testFixtures(project(":features:oo-sdk-android-rum")))
    testImplementation(testFixtures(project(":features:oo-sdk-android-trace")))
    testImplementation(libs.bundles.jUnit5)
    testImplementation(libs.bundles.testTools)
    testImplementation(libs.okHttpMock)
    unmock(libs.robolectric)
}

unMock {
    keepStartingWith("org.json")
    keepStartingWith("android.util.")
}

kotlinConfig(jvmBytecodeTarget = JvmTarget.JVM_11)
androidLibraryConfig()
junitConfig()
javadocConfig()
dependencyUpdateConfig()
publishingConfig(
    "An OkHttp monitoring integration to use with the OpenObserve monitoring library for Android applications."
)
detektCustomConfig()
