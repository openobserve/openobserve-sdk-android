/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

// CORE LIBRARY
include(":o2-sdk-android-core")
include(":o2-sdk-android-internal")

// MAIN FEATURE LIBRARIES
include(":features:o2-sdk-android-trace-api")
include(":features:o2-sdk-android-trace-internal")
include(":features:o2-sdk-android-rum")
include(":features:o2-sdk-android-rum-debug-widget")
include(":features:o2-sdk-android-logs")
include(":features:o2-sdk-android-ndk")
include(":features:o2-sdk-android-trace")
include(":features:o2-sdk-android-webview")
include(":features:o2-sdk-android-session-replay")
include(":features:o2-sdk-android-session-replay-compose")
include(":features:o2-sdk-android-session-replay-material")
include(":features:o2-sdk-android-trace-otel")
include(":features:o2-sdk-android-flags")
include(":features:o2-sdk-android-flags-openfeature")
include(":features:o2-sdk-android-profiling")

// INTEGRATION LIBRARIES
include(":integrations:o2-sdk-android-apollo")
include(":integrations:o2-sdk-android-coil")
include(":integrations:o2-sdk-android-coil3")
include(":integrations:o2-sdk-android-compose")
include(":integrations:o2-sdk-android-cronet")
include(":integrations:o2-sdk-android-fresco")
include(":integrations:o2-sdk-android-glide")
include(":integrations:o2-sdk-android-rx")
include(":integrations:o2-sdk-android-sqldelight")
include(":integrations:o2-sdk-android-timber")
include(":integrations:o2-sdk-android-tv")
include(":integrations:o2-sdk-android-okhttp")
include(":integrations:o2-sdk-android-okhttp-otel")
include(":integrations:o2-sdk-android-rum-coroutines")
include(":integrations:o2-sdk-android-trace-coroutines")

// TESTING UTILS
include(":reliability:stub-core")
include(":reliability:stub-feature")

// SINGLE FEATURE INTEGRATION TESTS
include(":reliability:single-fit:logs")
include(":reliability:single-fit:rum")
include(":reliability:single-fit:trace")
include(":reliability:single-fit:okhttp")

// CORE INTEGRATION TESTS
include(":reliability:core-it")

// LEGACY TESTS
include(":instrumented:integration")

// SAMPLE PROJECTS
include(":sample:kotlin")
include(":sample:tv")
include(":sample:wear")
include(":sample:vendor-lib")
include(":sample:benchmark")
include(":sample:automotive")

// TOOLCHAIN
include(":tools:detekt")
include(":tools:unit")
include(":tools:noopfactory")
include(":tools:javabackport")
include(":tools:lint")
include(":tools:benchmark")
