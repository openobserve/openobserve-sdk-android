/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.compose.test.elmyr

import com.openobserve.android.sessionreplay.compose.internal.data.ComposeWireframe
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class ComposeWireframeForgeryFactory : ForgeryFactory<ComposeWireframe> {
    override fun getForgery(forge: Forge): ComposeWireframe {
        return ComposeWireframe(
            forge.getForgery(),
            forge.aNullable { getForgery() }
        )
    }
}
