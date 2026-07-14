/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.compose.internal.data

import android.graphics.Bitmap
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale

internal data class BitmapInfo(
    val bitmap: Bitmap,
    val isContextualImage: Boolean,
    val contentScale: ContentScale? = null,
    val alignment: Alignment? = null
)
