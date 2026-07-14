/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.feature.event

/**
 * Thread information to be propagated on the message bus to the features doing crash reporting.
 * Is not a part of official API.
 */
@Suppress("UndocumentedPublicClass", "UndocumentedPublicProperty")
data class ThreadDump(
    val name: String,
    val state: String,
    val stack: String,
    val crashed: Boolean
)
