/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.internal.recorder.resources

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import com.openobserve.android.internal.utils.densityNormalized
import com.openobserve.android.sessionreplay.IMAGE_DIMEN_CONSIDERED_PII_IN_DP

internal class ImageTypeResolver {
    fun isDrawablePII(drawable: Drawable, density: Float): Boolean {
        val isNotGradient = drawable !is GradientDrawable
        val widthDp = drawable.intrinsicWidth.densityNormalized(density)
        val heightDp = drawable.intrinsicHeight.densityNormalized(density)

        return isNotGradient && isPIIByDimensions(widthDp, heightDp)
    }

    fun isPIIByDimensions(width: Int, height: Int): Boolean {
        val isGreaterThan = width >= IMAGE_DIMEN_CONSIDERED_PII_IN_DP || height >= IMAGE_DIMEN_CONSIDERED_PII_IN_DP
        return isGreaterThan
    }
}
