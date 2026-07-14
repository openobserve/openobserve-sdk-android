/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.insights.internal.platform

import android.os.Debug

internal class Platform {
    fun getRuntimeStat(name: String): String? =
        Debug.getRuntimeStat(name)

    fun getNativeHeapAllocatedSize(): Long =
        Debug.getNativeHeapAllocatedSize()

    @Suppress("PreferTimeProvider")
    fun nanoTime(): Long = System.nanoTime()
}
