/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.forge

import com.openobserve.android.sessionreplay.model.MobileSegment
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class WebViewWireframeForgeryFactory :
    ForgeryFactory<MobileSegment.Wireframe.WebviewWireframe> {
    override fun getForgery(forge: Forge): MobileSegment.Wireframe.WebviewWireframe {
        return MobileSegment.Wireframe.WebviewWireframe(
            id = forge.aPositiveInt().toLong(),
            x = forge.aPositiveLong(),
            y = forge.aPositiveLong(),
            width = forge.aPositiveLong(strict = true),
            height = forge.aPositiveLong(strict = true),
            shapeStyle = forge.aNullable { getForgery() },
            slotId = forge.aPositiveLong().toString()
        )
    }
}
