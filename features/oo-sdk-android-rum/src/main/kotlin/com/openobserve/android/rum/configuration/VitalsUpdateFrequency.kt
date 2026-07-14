/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.configuration

/**
 * Defines the frequency at which mobile vitals monitor updates the data.
 */
@Suppress("MagicNumber")
enum class VitalsUpdateFrequency(
    internal val periodInMs: Long
) {

    /** Every 100 milliseconds. */
    FREQUENT(100L),

    /** Every 500 milliseconds. This is the default frequency. */
    AVERAGE(500L),

    /** Every 1000 milliseconds. */
    RARE(1000L),

    /** No data will be sent for mobile vitals. */
    NEVER(0)
}
