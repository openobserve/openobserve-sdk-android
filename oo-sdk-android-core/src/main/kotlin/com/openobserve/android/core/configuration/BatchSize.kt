/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.configuration

/**
 * Defines the policy when batching data together.
 * Smaller batches means smaller but more network requests,
 * whereas larger batches will mean fewer but larger network requests.
 */
@Suppress("MagicNumber")
enum class BatchSize(
    internal val windowDurationMs: Long
) {

    /** Prefer small batches. **/
    SMALL(3000L),

    /** Prefer medium-sized batches. **/
    MEDIUM(10000L),

    /** Prefer large batches. **/
    LARGE(35000L)
}
