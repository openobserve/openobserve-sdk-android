/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

import com.openobserve.gradle.config.AndroidConfig
import com.openobserve.gradle.config.configureFlavorForTvApp
import com.openobserve.gradle.config.dependencyUpdateConfig
import com.openobserve.gradle.config.java17
import com.openobserve.gradle.config.junitConfig
import com.openobserve.gradle.config.kotlinConfig
import com.openobserve.gradle.config.taskConfig

plugins {
    id("com.android.application")
    kotlin("android")
    id("com.github.ben-manes.versions")
    // openobserve: oo-sdk-android-gradle-plugin fork (openobserve-sdk-android-gradle-plugin, doc 06 #7) not yet published; ProGuard-upload plugin not needed to build the sample.

    // alias(libs.plugins.ooGradlePlugin)
}

android {
    namespace = "com.openobserve.android.tv.sample"
    compileSdk = AndroidConfig.TARGET_SDK
    buildToolsVersion = AndroidConfig.BUILD_TOOLS_VERSION

    defaultConfig {
        minSdk = AndroidConfig.MIN_SDK
        targetSdk = AndroidConfig.TARGET_SDK
        versionCode = AndroidConfig.VERSION.code
        versionName = AndroidConfig.VERSION.name
        multiDexEnabled = true

        vectorDrawables.useSupportLibrary = true

        configureFlavorForTvApp(project.rootDir)
    }

    compileOptions {
        java17()
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        buildConfig = true
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
}

repositories {
    maven { setUrl("https://jitpack.io") }
}

dependencies {

    implementation(project(":oo-sdk-android-core"))
    implementation(project(":features:oo-sdk-android-rum"))
    implementation(project(":features:oo-sdk-android-logs"))
    implementation(project(":features:oo-sdk-android-session-replay"))
    implementation(project(":features:oo-sdk-android-session-replay-material"))
    implementation(project(":integrations:oo-sdk-android-okhttp"))
    implementation(project(":integrations:oo-sdk-android-timber"))
    implementation(project(":integrations:oo-sdk-android-tv"))

    implementation(libs.kotlin)

    // Android dependencies
    implementation(libs.androidXCore)
    implementation(libs.androidXCoreKtx)
    implementation(libs.androidXAppCompat)
    implementation(libs.googleMaterial)
    implementation(libs.androidXRecyclerView)
    implementation(libs.androidXConstraintLayout)
    implementation(libs.androidXLifecycleLiveDataKtx)
    implementation(libs.androidXLifecycleViewModelKtx)
    implementation(libs.bundles.androidXNavigation)
    implementation(libs.androidXNavigationUIKtx)

    // Network
    implementation(libs.okHttp)
    implementation(libs.gson)

    // Misc
    implementation(libs.timber)

    // Video
    implementation(libs.bundles.exoplayer)
    implementation(libs.newPipeExtractor)
}

kotlinConfig(evaluateWarningsAsErrors = false)
taskConfig<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        optIn.add("kotlin.RequiresOptIn")
    }
}
junitConfig()
dependencyUpdateConfig()
