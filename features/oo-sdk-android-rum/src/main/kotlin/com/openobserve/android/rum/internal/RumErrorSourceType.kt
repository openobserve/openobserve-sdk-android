/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.internal

/**
 * Source type of the error (the language or platform impacting the error stacktrace format).
 * @see [RumMonitor]
 */
internal enum class RumErrorSourceType {
    ANDROID,
    BROWSER,
    REACT_NATIVE,
    FLUTTER,
    NDK,
    NDK_IL2CPP
}
