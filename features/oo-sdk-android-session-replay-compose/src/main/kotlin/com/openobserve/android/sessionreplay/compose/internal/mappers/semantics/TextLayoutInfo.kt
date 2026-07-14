/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.compose.internal.mappers.semantics

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import com.openobserve.android.sessionreplay.model.MobileSegment

internal data class TextLayoutInfo(
    val text: String,
    val color: ULong,
    val fontSize: Long,
    val fontFamily: FontFamily?,
    val textAlign: TextAlign? = TextAlign.Start,
    val textOverflow: MobileSegment.TruncationMode? = null
)
