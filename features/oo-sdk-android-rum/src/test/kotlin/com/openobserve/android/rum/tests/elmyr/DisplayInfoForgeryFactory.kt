/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.tests.elmyr

import com.openobserve.android.rum.internal.domain.display.DisplayInfo
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class DisplayInfoForgeryFactory : ForgeryFactory<DisplayInfo> {
    override fun getForgery(forge: Forge): DisplayInfo {
        return DisplayInfo(
            screenBrightness = forge.aNullable { aFloat() }
        )
    }
}
