/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.internal.processor

internal data class WireframeBounds(
    val left: Long,
    val right: Long,
    val top: Long,
    val bottom: Long,
    val width: Long,
    val height: Long
)
