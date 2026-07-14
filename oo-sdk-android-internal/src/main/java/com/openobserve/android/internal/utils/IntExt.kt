/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.internal.utils

/**
 * Normalizes an Int value (font size, view dimension, view position, etc.) according to the
 * device pixels density.
 * Example: if a device has a DPI = 2, the normalized height of a view will be
 * view.height/2.
 * @param density
 */
fun Int.densityNormalized(density: Float): Int {
    if (density == 0f) {
        return this
    }
    return (this / density).toInt()
}
