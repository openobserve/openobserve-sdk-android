/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

// CORE LIBRARY
include(":oo-sdk-android-core")
include(":oo-sdk-android-internal")

// MAIN FEATURE LIBRARIES
include(":features:oo-sdk-android-trace-api")
include(":features:oo-sdk-android-trace-internal")
include(":features:oo-sdk-android-rum")
include(":features:oo-sdk-android-rum-debug-widget")
include(":features:oo-sdk-android-logs")
include(":features:oo-sdk-android-ndk")
include(":features:oo-sdk-android-trace")
include(":features:oo-sdk-android-webview")
include(":features:oo-sdk-android-session-replay")
include(":features:oo-sdk-android-session-replay-compose")
include(":features:oo-sdk-android-session-replay-material")
include(":features:oo-sdk-android-trace-otel")
include(":features:oo-sdk-android-flags")
include(":features:oo-sdk-android-flags-openfeature")
include(":features:oo-sdk-android-profiling")

// INTEGRATION LIBRARIES
include(":integrations:oo-sdk-android-apollo")
include(":integrations:oo-sdk-android-coil")
include(":integrations:oo-sdk-android-coil3")
include(":integrations:oo-sdk-android-compose")
include(":integrations:oo-sdk-android-cronet")
include(":integrations:oo-sdk-android-fresco")
include(":integrations:oo-sdk-android-glide")
include(":integrations:oo-sdk-android-rx")
include(":integrations:oo-sdk-android-sqldelight")
include(":integrations:oo-sdk-android-timber")
include(":integrations:oo-sdk-android-tv")
include(":integrations:oo-sdk-android-okhttp")
include(":integrations:oo-sdk-android-okhttp-otel")
include(":integrations:oo-sdk-android-rum-coroutines")
include(":integrations:oo-sdk-android-trace-coroutines")

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
