/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.internal.time

internal interface AppStartTimeProvider {
    /**
     * Provide the time the application started in nanoseconds from device boot, or our best guess
     * if the actual start time is not available.
     */
    val appStartTimeNs: Long

    /**
     * Provide the time since the application started in nanoseconds from device boot, or our best guess
     * if the actual start time is not available.
     */
    val appUptimeNs: Long
}
