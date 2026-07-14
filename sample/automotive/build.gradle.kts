/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

import com.openobserve.gradle.config.AndroidConfig
import com.openobserve.gradle.config.configureFlavorForAutoApp
import com.openobserve.gradle.config.dependencyUpdateConfig
import com.openobserve.gradle.config.java17
import com.openobserve.gradle.config.junitConfig
import com.openobserve.gradle.config.kotlinConfig
import com.openobserve.gradle.config.taskConfig
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    kotlin("android")
    id("com.github.ben-manes.versions")
    alias(libs.plugins.ooGradlePlugin)
}

android {
    compileSdk = AndroidConfig.TARGET_SDK
    buildToolsVersion = AndroidConfig.BUILD_TOOLS_VERSION

    defaultConfig {
        minSdk = AndroidConfig.MIN_SDK_FOR_AUTO
        targetSdk = AndroidConfig.TARGET_SDK
        versionCode = AndroidConfig.VERSION.code
        versionName = AndroidConfig.VERSION.name
        multiDexEnabled = true

        buildFeatures {
            buildConfig = true
        }

        configureFlavorForAutoApp(project.rootDir)
    }

    namespace = "com.openobserve.sample.automotive"

    compileOptions {
        java17()
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
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
            isMinifyEnabled = false
        }
    }
}

dependencies {
    // OpenObserve Libraries
    implementation(project(":features:oo-sdk-android-logs"))
    implementation(project(":features:oo-sdk-android-rum"))

    implementation(libs.kotlin)

    // Android dependencies
    implementation(libs.androidXMultidex)
    implementation(libs.androidXCoreKtx)
    implementation(libs.androidXAppCompat)
    implementation(libs.androidXLegacySupportV4)
    implementation(libs.androidXLegacySupportV13)

    // Android Car
    implementation(libs.androidXCarApp)
    implementation(libs.androidXCarAutomotive)
}

kotlinConfig(evaluateWarningsAsErrors = false)
taskConfig<KotlinCompile> {
    compilerOptions {
        optIn.add("kotlin.RequiresOptIn")
    }
}
junitConfig()
dependencyUpdateConfig()
