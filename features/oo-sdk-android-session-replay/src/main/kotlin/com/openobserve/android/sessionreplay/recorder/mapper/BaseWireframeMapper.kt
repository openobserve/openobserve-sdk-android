/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.recorder.mapper

import android.graphics.drawable.Drawable
import android.view.View
import com.openobserve.android.api.InternalLogger
import com.openobserve.android.sessionreplay.model.MobileSegment
import com.openobserve.android.sessionreplay.utils.ColorStringFormatter
import com.openobserve.android.sessionreplay.utils.DrawableToColorMapper
import com.openobserve.android.sessionreplay.utils.ViewBoundsResolver
import com.openobserve.android.sessionreplay.utils.ViewIdentifierResolver

/**
 * A basic abstract [WireframeMapper] that provides some helpful utilities.
 *
 * It provides functions to:
 *  - resolve a stable id for any [View]
 *  - converts a [Drawable] into a [MobileSegment.ShapeStyle]
 *
 *  @param T the type of the [View] to map
 *  @property viewIdentifierResolver the [ViewIdentifierResolver] (to resolve a view or children stable id)
 *  @property colorStringFormatter the [ColorStringFormatter] to transform Color into HTML hex strings
 *  @property viewBoundsResolver the [ViewBoundsResolver] to get a view boundaries in density independent units
 *  @property drawableToColorMapper the [DrawableToColorMapper] to convert a background drawable into a solid color
 */
abstract class BaseWireframeMapper<in T : View>(
    protected val viewIdentifierResolver: ViewIdentifierResolver,
    protected val colorStringFormatter: ColorStringFormatter,
    protected val viewBoundsResolver: ViewBoundsResolver,
    protected val drawableToColorMapper: DrawableToColorMapper
) : WireframeMapper<T> {

    /**
     * Resolves the [View] unique id to be used in the mapped [MobileSegment.Wireframe].
     */
    protected fun resolveViewId(view: View): Long {
        return viewIdentifierResolver.resolveViewId(view)
    }

    /**
     * Resolves the [MobileSegment.ShapeStyle] based on the [View] drawables.
     */
    protected fun resolveShapeStyle(
        drawable: Drawable,
        viewAlpha: Float,
        internalLogger: InternalLogger
    ): MobileSegment.ShapeStyle? {
        val color = drawableToColorMapper.mapDrawableToColor(drawable, internalLogger)
        return if (color != null) {
            MobileSegment.ShapeStyle(colorStringFormatter.formatColorAsHexString(color), viewAlpha)
        } else {
            null
        }
    }
}
