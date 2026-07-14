/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

import com.openobserve.gradle.config.AndroidConfig
import com.openobserve.gradle.config.java17
import com.openobserve.gradle.config.kotlinConfig

plugins {
    id("com.android.application")
    kotlin("android")
}

android {

    compileSdk = AndroidConfig.TARGET_SDK
    buildToolsVersion = AndroidConfig.BUILD_TOOLS_VERSION

    @Suppress("MagicNumber")
    defaultConfig {
        minSdk = AndroidConfig.MIN_SDK
        targetSdk = AndroidConfig.TARGET_SDK
        versionCode = 42
        versionName = "4.2.13"

        buildFeatures {
            buildConfig = true
        }

        multiDexEnabled = true
        vectorDrawables.useSupportLibrary = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    namespace = "com.openobserve.android.sdk.integration"

    testOptions {
        unitTests.isReturnDefaultValues = true
    }

    sourceSets.named("main") {
        java.srcDir("src/main/kotlin")
    }
    sourceSets.named("test") {
        java.srcDir("src/test/kotlin")
    }
    sourceSets.named("androidTest") {
        java.srcDir("src/androidTest/kotlin")
    }

    compileOptions {
        java17()
        if (project.hasProperty(com.openobserve.gradle.Properties.USE_DESUGARING)) {
            isCoreLibraryDesugaringEnabled = true
        }
    }

    packaging {
        resources {
            excludes += listOf(
                "META-INF/jvm.kotlin_module",
                "META-INF/LICENSE.md",
                "META-INF/LICENSE-notice.md"
            )
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            testProguardFile("test-proguard-rules.pro")
        }
    }
}

repositories {
    google()
    mavenLocal()
    mavenCentral()
}

dependencies {
    if (project.hasProperty(com.openobserve.gradle.Properties.USE_DESUGARING)) {
        coreLibraryDesugaring(libs.androidDesugaringSdk)
    }
    implementation(project(":features:oo-sdk-android-session-replay"))
    implementation(project(":features:oo-sdk-android-logs"))
    implementation(project(":features:oo-sdk-android-trace"))
    implementation(project(":features:oo-sdk-android-rum"))
    implementation(project(":oo-sdk-android-internal"))
    implementation(project(":integrations:oo-sdk-android-okhttp"))
    implementation(project(":integrations:oo-sdk-android-cronet"))
    implementation(libs.cronetPlayServices)

    implementation(libs.gson)
    implementation(libs.kotlin)
    implementation(libs.bundles.androidXSupportBase)
    implementation(libs.androidXMultidex)
    implementation(libs.elmyr)
    implementation(libs.leakCanaryAndroid)

    androidTestImplementation(project(":oo-sdk-android-internal"))
    androidTestImplementation(libs.leakCanaryInstrumentation)
    androidTestImplementation(project(":tools:unit")) {
        attributes {
            attribute(
                com.android.build.api.attributes.ProductFlavorAttr.of("platform"),
                objects.named("art")
            )
        }
    }
    androidTestImplementation(libs.assertJ)
    androidTestImplementation(libs.bundles.integrationTests)
    androidTestImplementation(libs.okHttpMock)
    androidTestImplementation(project(":features:oo-sdk-android-trace-internal"))
    androidTestImplementation(testFixtures(project(":features:oo-sdk-android-trace")))
    if (project.hasProperty(com.openobserve.gradle.Properties.USE_API21_JAVA_BACKPORT)) {
        // this is needed to make AssertJ working on APIs <24
        androidTestImplementation(project(":tools:javabackport"))
    }
}

kotlinConfig()
