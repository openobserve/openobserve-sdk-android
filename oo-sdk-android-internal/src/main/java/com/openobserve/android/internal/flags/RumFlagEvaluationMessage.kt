/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.internal.flags

/**
 * Event for sending a RUM evaluation.
 * @param flagKey name of the flag.
 * @param value value of the flag.
 */
data class RumFlagEvaluationMessage(
    val flagKey: String,
    val value: Any
)
