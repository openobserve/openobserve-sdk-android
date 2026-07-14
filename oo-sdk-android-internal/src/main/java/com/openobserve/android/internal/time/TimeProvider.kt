/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.internal.time

import android.os.SystemClock

// there is no NoOpImplementation on purpose, we don't want to have 0 values for the
// case when this instance is used.
/**
 * Interface to provide the current time in both device and server time references.
 */
interface TimeProvider {

    /**
     * Returns the current device timestamp in milliseconds.
     * This is implemented in [BaseTimeProvider] as [System.currentTimeMillis].
     */
    fun getDeviceTimestampMillis(): Long

    /**
     * Returns the current server timestamp in milliseconds.
     */
    fun getServerTimestampMillis(): Long

    /**
     * Returns the current device monotonic elapsed time in nanoseconds.
     * This is implemented in [BaseTimeProvider] as [System.nanoTime].
     */
    fun getDeviceElapsedTimeNanos(): Long

    /**
     * Returns the offset between the device and server time references in nanoseconds.
     */
    fun getServerOffsetNanos(): Long

    /**
     * Returns the offset between the device and server time references in milliseconds.
     */
    fun getServerOffsetMillis(): Long

    /**
     * Returns the time since boot in milliseconds, including time spent in sleep.
     * This is implemented in [BaseTimeProvider] as [SystemClock.elapsedRealtime].
     */
    fun getDeviceElapsedRealtimeMillis(): Long
}

/**
 * A base implementation of [TimeProvider] that provides the device time using system calls.
 */
abstract class BaseTimeProvider : TimeProvider {
    final override fun getDeviceTimestampMillis(): Long = System.currentTimeMillis()
    final override fun getDeviceElapsedTimeNanos(): Long = System.nanoTime()
    final override fun getDeviceElapsedRealtimeMillis(): Long = SystemClock.elapsedRealtime()
}
