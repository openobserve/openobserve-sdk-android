/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

import com.openobserve.gradle.config.AndroidConfig
import com.openobserve.gradle.config.configureFlavorForBenchmark
import com.openobserve.gradle.config.dependencyUpdateConfig
import com.openobserve.gradle.config.java17
import com.openobserve.gradle.config.junitConfig
import com.openobserve.gradle.config.kotlinConfig
// openobserve: openobserve-sdk-android-gradle-plugin (#7) not yet published
// import com.openobserve.gradle.plugin.InstrumentationMode

plugins {
    id("com.android.application")
    kotlin("android")
    alias(libs.plugins.composeCompilerPlugin)
    alias(libs.plugins.kotlinSPGradlePlugin)
    kotlin("plugin.serialization")
    id("kotlin-parcelize")
    // openobserve: o2-sdk-android-gradle-plugin fork (openobserve-sdk-android-gradle-plugin, doc 06 #7) not yet published; ProGuard-upload plugin not needed to build the sample.

    // alias(libs.plugins.ooGradlePlugin)
    id("transitiveDependencies")
}

@Suppress("StringLiteralDuplication")
android {
    namespace = "com.openobserve.sample.benchmark"
    compileSdk = AndroidConfig.TARGET_SDK
    buildToolsVersion = AndroidConfig.BUILD_TOOLS_VERSION

    defaultConfig {
        minSdk = AndroidConfig.MIN_SDK
        targetSdk = AndroidConfig.TARGET_SDK
        versionCode = AndroidConfig.VERSION.code
        versionName = AndroidConfig.VERSION.name
        multiDexEnabled = true

        buildFeatures {
            buildConfig = true
        }
        vectorDrawables.useSupportLibrary = true
        configureFlavorForBenchmark(project.rootDir)
    }
    compileOptions {
        java17()
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }
    val bmPassword = System.getenv("BM_STORE_PASSWD")
    signingConfigs {
        if (bmPassword != null) {
            create("release") {
                storeFile = File(project.rootDir, "sample-benchmark.keystore")
                storePassword = bmPassword
                keyAlias = "o2-sdk-android"
                keyPassword = bmPassword
            }
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }

        getByName("release") {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            isMinifyEnabled = true
            signingConfigs.findByName("release")?.let {
                signingConfig = it
            } ?: kotlin.run {
                signingConfig = signingConfigs.findByName("debug")
            }
        }
    }
}

// openobserve: gradle plugin (#7) disabled until published
// datadog {
//     composeInstrumentation = InstrumentationMode.AUTO
// }

dependencies {

    implementation(libs.kotlin)

    // Android dependencies
    implementation(libs.adapterDelegatesViewBinding)
    implementation(libs.androidXMultidex)
    implementation(libs.bundles.androidXNavigation)
    implementation(libs.androidXAppCompat)
    implementation(libs.androidXConstraintLayout)
    implementation(libs.androidXLifecycleCompose)
    implementation(libs.googleMaterial)
    implementation(libs.bundles.glide)
    implementation(libs.timber)
    implementation(platform(libs.androidXComposeBom))
    implementation(libs.material3Android)
    implementation(libs.bundles.androidXCompose)
    implementation(libs.coilCompose)
    implementation(libs.daggerLib)
    ksp(libs.daggerCompiler)
    ksp(libs.glideKsp)
    implementation(libs.coroutinesCore)
    implementation(libs.bundles.ktorClient)
    implementation(libs.kotlinxSerializationJson)
    implementation(project(":features:o2-sdk-android-logs"))
    implementation(project(":features:o2-sdk-android-rum"))
    implementation(project(":features:o2-sdk-android-trace"))
    implementation(project(":features:o2-sdk-android-trace-otel"))
    implementation(project(":features:o2-sdk-android-ndk"))
    implementation(project(":features:o2-sdk-android-webview"))
    implementation(project(":features:o2-sdk-android-session-replay"))
    implementation(project(":features:o2-sdk-android-session-replay-material"))
    implementation(project(":features:o2-sdk-android-session-replay-compose"))
    implementation(project(":integrations:o2-sdk-android-compose"))
    implementation(project(":integrations:o2-sdk-android-glide"))
    implementation(project(":integrations:o2-sdk-android-okhttp"))
    implementation(project(":tools:benchmark"))

    testImplementation(libs.bundles.jUnit5)
    testImplementation(libs.bundles.testTools)
    testImplementation(libs.systemStubsJupiter)
    testImplementation(libs.ktorClientMock)
}

kotlinConfig()
junitConfig()
dependencyUpdateConfig()
