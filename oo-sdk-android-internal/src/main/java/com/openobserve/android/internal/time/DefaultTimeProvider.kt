/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.internal.time

/**
 * A [TimeProvider] implementation that provides the current device time as both device and server time.
 * The offsets are always 0.
 *
 * Device timestamp and elapsed time are inherited from [BaseTimeProvider].
 */
class DefaultTimeProvider : BaseTimeProvider() {
    override fun getServerTimestampMillis(): Long = getDeviceTimestampMillis()

    override fun getServerOffsetNanos(): Long = 0L

    override fun getServerOffsetMillis(): Long = 0L
}
