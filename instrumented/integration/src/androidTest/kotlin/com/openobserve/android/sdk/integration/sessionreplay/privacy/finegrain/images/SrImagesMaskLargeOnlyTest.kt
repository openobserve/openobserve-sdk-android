/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sdk.integration.sessionreplay.privacy.finegrain.images

import com.openobserve.android.privacy.TrackingConsent
import com.openobserve.android.sdk.integration.sessionreplay.SessionReplayImagesMixedSizesActivity
import com.openobserve.android.sdk.integration.sessionreplay.privacy.ImagePrivacyTestBase
import com.openobserve.android.sdk.rules.SessionReplayTestRule
import com.openobserve.android.sdk.utils.SR_IMAGE_PRIVACY
import com.openobserve.android.sessionreplay.ImagePrivacy
import org.junit.Rule
import org.junit.Test

internal class SrImagesMaskLargeOnlyTest :
    ImagePrivacyTestBase<SessionReplayImagesMixedSizesActivity>() {

    @get:Rule
    override val rule = SessionReplayTestRule(
        SessionReplayImagesMixedSizesActivity::class.java,
        trackingConsent = TrackingConsent.GRANTED,
        keepRequests = true,
        intentExtras = mapOf(
            SR_IMAGE_PRIVACY to ImagePrivacy.MASK_LARGE_ONLY
        )
    )

    @Test
    fun assessRecordedScreenPayloadMaskLargeOnly() {
        assertMixedImageWireframes(
            rule = rule,
            expectedPlaceholderCount = 1,
            expectedImageCount = 1
        )
    }
}
