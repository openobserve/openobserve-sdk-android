/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.compose.test.elmyr

import com.openobserve.android.sessionreplay.utils.GlobalBounds
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class GlobalBoundsForgeryFactory : ForgeryFactory<GlobalBounds> {
    override fun getForgery(forge: Forge): GlobalBounds {
        return GlobalBounds(
            x = forge.aLong(min = 128L, max = 65536L),
            y = forge.aLong(min = 128L, max = 65536L),
            width = forge.aLong(min = 32L, max = 65536L),
            height = forge.aLong(min = 32L, max = 65536L)
        )
    }
}
