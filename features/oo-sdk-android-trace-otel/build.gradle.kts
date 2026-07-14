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
    defaultConfig {
        buildFeatures {
            buildConfig = true
        }
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField(
            "String",
            "OPENTELEMETRY_API_VERSION_NAME",
            "\"${libs.versions.openTelemetry.get()}\""
        )
    }
    namespace = "com.openobserve.android.trace.opentelemetry"

    libraryVariants.all {
        packageLibraryProvider.configure {
            from("src/main/resources") {
                include("META-INF/**/verification.properties")
            }
        }
    }
}

dependencies {
    api(libs.openTelemetryApi)
    implementation(libs.kotlin)
    implementation(libs.androidXAnnotation)

    api(project(":oo-sdk-android-core"))
    implementation(project(":oo-sdk-android-internal"))
    implementation(project(":features:oo-sdk-android-trace"))

    testImplementation(project(":tools:unit")) {
        attributes {
            attribute(
                com.android.build.api.attributes.ProductFlavorAttr.of("platform"),
                objects.named("jvm")
            )
        }
    }
    testImplementation(testFixtures(project(":oo-sdk-android-core")))
    testImplementation(testFixtures(project(":features:oo-sdk-android-trace")))
    testImplementation(libs.bundles.jUnit5)
    testImplementation(libs.bundles.testTools)
    testImplementation(libs.systemStubsJupiter)

    unmock(libs.robolectric)
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
    "The tracing library for Android, providing OpenTelemetry compatibility."
)
detektCustomConfig()
