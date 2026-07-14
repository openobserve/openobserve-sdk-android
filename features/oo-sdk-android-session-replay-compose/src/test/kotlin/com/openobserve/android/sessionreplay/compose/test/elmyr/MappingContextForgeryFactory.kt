/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.compose.test.elmyr

import com.openobserve.android.sessionreplay.recorder.MappingContext
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory
import org.mockito.Mockito.mock

internal class MappingContextForgeryFactory : ForgeryFactory<MappingContext> {
    override fun getForgery(forge: Forge): MappingContext {
        return MappingContext(
            systemInformation = forge.getForgery(),
            imageWireframeHelper = mock(),
            hasOptionSelectorParent = forge.aBool(),
            imagePrivacy = forge.getForgery(),
            textAndInputPrivacy = forge.getForgery(),
            touchPrivacyManager = mock(),
            interopViewCallback = mock()
        )
    }
}
