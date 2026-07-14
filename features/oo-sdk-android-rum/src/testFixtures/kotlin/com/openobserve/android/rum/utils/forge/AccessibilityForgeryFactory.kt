/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.utils.forge

import com.openobserve.android.rum.model.ViewEvent.Accessibility
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class AccessibilityForgeryFactory : ForgeryFactory<Accessibility> {
    override fun getForgery(forge: Forge): Accessibility {
        return Accessibility(
            textSize = forge.aNullable { aString() },
            rtlEnabled = forge.aNullable { aBool() },
            screenReaderEnabled = forge.aNullable { aBool() },
            increaseContrastEnabled = forge.aNullable { aBool() },
            reducedAnimationsEnabled = forge.aNullable { aBool() },
            invertColorsEnabled = forge.aNullable { aBool() },
            singleAppModeEnabled = forge.aNullable { aBool() }
        )
    }
}
