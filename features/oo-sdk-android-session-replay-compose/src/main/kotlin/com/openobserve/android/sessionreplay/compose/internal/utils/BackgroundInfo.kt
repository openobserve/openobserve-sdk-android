/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.compose.internal.utils

import com.openobserve.android.sessionreplay.utils.GlobalBounds

internal data class BackgroundInfo(
    val globalBounds: GlobalBounds = GlobalBounds(0L, 0L, 0L, 0L),
    val color: Long? = null,
    val cornerRadius: Float = 0f
)
