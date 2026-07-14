/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.forge

import com.openobserve.android.sessionreplay.model.MobileSegment
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class WireframeForgeryFactory : ForgeryFactory<MobileSegment.Wireframe> {
    override fun getForgery(forge: Forge): MobileSegment.Wireframe {
        return when (forge.anInt(min = 0, max = 4)) {
            0 -> forge.getForgery<MobileSegment.Wireframe.ShapeWireframe>()
            1 -> forge.getForgery<MobileSegment.Wireframe.ImageWireframe>()
            2 -> forge.getForgery<MobileSegment.Wireframe.PlaceholderWireframe>()
            else -> forge.getForgery<MobileSegment.Wireframe.TextWireframe>()
        }
    }
}
