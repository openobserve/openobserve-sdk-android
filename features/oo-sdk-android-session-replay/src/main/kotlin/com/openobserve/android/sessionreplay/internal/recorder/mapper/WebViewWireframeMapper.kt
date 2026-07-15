/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.sessionreplay.internal.recorder.mapper

import android.webkit.WebView
import androidx.annotation.UiThread
import com.openobserve.android.api.InternalLogger
import com.openobserve.android.sessionreplay.model.MobileSegment
import com.openobserve.android.sessionreplay.recorder.MappingContext
import com.openobserve.android.sessionreplay.recorder.mapper.BaseWireframeMapper
import com.openobserve.android.sessionreplay.utils.AsyncJobStatusCallback
import com.openobserve.android.sessionreplay.utils.ColorStringFormatter
import com.openobserve.android.sessionreplay.utils.DrawableToColorMapper
import com.openobserve.android.sessionreplay.utils.ViewBoundsResolver
import com.openobserve.android.sessionreplay.utils.ViewIdentifierResolver

internal class WebViewWireframeMapper(
    viewIdentifierResolver: ViewIdentifierResolver,
    colorStringFormatter: ColorStringFormatter,
    viewBoundsResolver: ViewBoundsResolver,
    drawableToColorMapper: DrawableToColorMapper
) : BaseWireframeMapper<WebView>(
    viewIdentifierResolver,
    colorStringFormatter,
    viewBoundsResolver,
    drawableToColorMapper
) {

    @UiThread
    override fun map(
        view: WebView,
        mappingContext: MappingContext,
        asyncJobStatusCallback: AsyncJobStatusCallback,
        internalLogger: InternalLogger
    ): List<MobileSegment.Wireframe> {
        val viewGlobalBounds = viewBoundsResolver.resolveViewGlobalBounds(
            view,
            mappingContext.systemInformation.screenDensity
        )
        val webViewId = resolveViewId(view)
        return listOf(
            MobileSegment.Wireframe.WebviewWireframe(
                webViewId,
                viewGlobalBounds.x,
                viewGlobalBounds.y,
                viewGlobalBounds.width,
                viewGlobalBounds.height,
                slotId = webViewId.toString(),
                isVisible = true
            )
        )
    }
}
