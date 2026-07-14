/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.material.internal

import androidx.cardview.widget.CardView
import com.openobserve.android.api.InternalLogger
import com.openobserve.android.sessionreplay.model.MobileSegment
import com.openobserve.android.sessionreplay.recorder.MappingContext
import com.openobserve.android.sessionreplay.recorder.mapper.BaseViewGroupMapper
import com.openobserve.android.sessionreplay.utils.AsyncJobStatusCallback
import com.openobserve.android.sessionreplay.utils.ColorStringFormatter
import com.openobserve.android.sessionreplay.utils.DrawableToColorMapper
import com.openobserve.android.sessionreplay.utils.ViewBoundsResolver
import com.openobserve.android.sessionreplay.utils.ViewIdentifierResolver
import com.google.android.material.card.MaterialCardView

internal class CardWireframeMapper(
    viewIdentifierResolver: ViewIdentifierResolver,
    colorStringFormatter: ColorStringFormatter,
    viewBoundsResolver: ViewBoundsResolver,
    drawableToColorMapper: DrawableToColorMapper
) : BaseViewGroupMapper<CardView>(
    viewIdentifierResolver,
    colorStringFormatter,
    viewBoundsResolver,
    drawableToColorMapper
) {
    override fun map(
        view: CardView,
        mappingContext: MappingContext,
        asyncJobStatusCallback: AsyncJobStatusCallback,
        internalLogger: InternalLogger
    ): List<MobileSegment.Wireframe> {
        val viewGlobalBounds = viewBoundsResolver.resolveViewGlobalBounds(
            view,
            mappingContext.systemInformation.screenDensity
        )
        val shapeStyle = resolveShapeStyle(view, mappingContext)

        // Only MaterialCardView can have a built-in border.
        val shapeBorder = if (view is MaterialCardView) {
            resolveShapeBorder(view, mappingContext)
        } else {
            null
        }

        return listOf(
            MobileSegment.Wireframe.ShapeWireframe(
                resolveViewId(view),
                viewGlobalBounds.x,
                viewGlobalBounds.y,
                viewGlobalBounds.width,
                viewGlobalBounds.height,
                shapeStyle = shapeStyle,
                border = shapeBorder
            )
        )
    }

    private fun resolveShapeBorder(
        view: MaterialCardView,
        mappingContext: MappingContext
    ): MobileSegment.ShapeBorder {
        @Suppress("DEPRECATION")
        val strokeColor = view.strokeColorStateList?.defaultColor ?: view.strokeColor
        return MobileSegment.ShapeBorder(
            color = colorStringFormatter.formatColorAsHexString(strokeColor),
            width = view.strokeWidth.toLong().densityNormalized(mappingContext.systemInformation.screenDensity)
        )
    }

    private fun resolveShapeStyle(
        view: CardView,
        mappingContext: MappingContext
    ): MobileSegment.ShapeStyle {
        val backgroundColor = view.cardBackgroundColor.defaultColor
        return MobileSegment.ShapeStyle(
            backgroundColor = colorStringFormatter.formatColorAsHexString(backgroundColor),
            opacity = view.alpha,
            cornerRadius = view.radius.toLong().densityNormalized(mappingContext.systemInformation.screenDensity)
        )
    }
}
