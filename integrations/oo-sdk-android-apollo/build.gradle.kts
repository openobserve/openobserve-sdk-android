/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

import com.openobserve.gradle.config.androidLibraryConfig
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
    id("org.jetbrains.dokka")

    // Analysis tools
    id("com.github.ben-manes.versions")

    // Tests
    id("de.mobilej.unmock")
    id("org.jetbrains.kotlinx.kover")

    // Internal Generation
    id("apiSurface")
    id("transitiveDependencies")
    id("verificationXml")
}

android {
    namespace = "com.openobserve.android.apollo"

    libraryVariants.all {
        packageLibraryProvider.configure {
            from("src/main/resources") {
                include("META-INF/**/verification.properties")
            }
        }
    }
}

dependencies {
    implementation(project(":oo-sdk-android-internal"))
    implementation(libs.apolloRuntime)
    implementation(libs.kotlin)
    implementation(libs.okHttp)

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
    testImplementation(libs.okHttpMock)
    unmock(libs.robolectric)
}

unMock {
    keepStartingWith("android.util.")
}

kotlinConfig(jvmBytecodeTarget = JvmTarget.JVM_11)
junitConfig()
androidLibraryConfig()
publishingConfig(
    projectDescription = "An Apollo interceptor for handling GraphQL requests to use with the " +
        "OpenObserve monitoring library for Android applications."
)
