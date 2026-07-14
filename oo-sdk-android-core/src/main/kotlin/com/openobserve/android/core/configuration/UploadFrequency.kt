/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.configuration

/**
 * Defines the frequency at which batch upload are tried.
 */
@Suppress("MagicNumber")
enum class UploadFrequency(
    internal val baseStepMs: Long
) {

    /** Try to upload batch data frequently. */
    FREQUENT(500L),

    /** Try to upload batch data with a medium frequency. */
    AVERAGE(2000L),

    /** Try to upload batch data rarely. */
    RARE(5000L)
}
