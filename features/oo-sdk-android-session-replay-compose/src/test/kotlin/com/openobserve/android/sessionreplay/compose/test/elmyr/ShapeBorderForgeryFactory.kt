/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.compose.test.elmyr

import com.openobserve.android.sessionreplay.model.MobileSegment
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class ShapeBorderForgeryFactory :
    ForgeryFactory<MobileSegment.ShapeBorder> {
    override fun getForgery(forge: Forge): MobileSegment.ShapeBorder {
        return MobileSegment.ShapeBorder(
            color = forge.aStringMatching("#[0-9A-Fa-f]{6}[fF]{2}"),
            width = forge.aPositiveLong(strict = true)
        )
    }
}
