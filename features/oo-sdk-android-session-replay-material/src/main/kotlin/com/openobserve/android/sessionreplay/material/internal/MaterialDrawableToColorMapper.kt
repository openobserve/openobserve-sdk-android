/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.material.internal

import android.graphics.drawable.Drawable
import com.openobserve.android.api.InternalLogger
import com.openobserve.android.sessionreplay.utils.DrawableToColorMapper
import com.google.android.material.shape.MaterialShapeDrawable

internal class MaterialDrawableToColorMapper : DrawableToColorMapper {

    override fun mapDrawableToColor(drawable: Drawable, internalLogger: InternalLogger): Int? {
        return when (drawable) {
            is MaterialShapeDrawable -> resolveMaterialShapeDrawable(drawable)
            else -> null
        }
    }

    private fun resolveMaterialShapeDrawable(
        shapeDrawable: MaterialShapeDrawable
    ): Int? {
        return shapeDrawable.fillColor?.defaultColor
    }
}
