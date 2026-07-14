/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sdk.integration.sessionreplay.privacy.finegrain.text

import com.openobserve.android.privacy.TrackingConsent
import com.openobserve.android.sdk.integration.sessionreplay.SessionReplayTextAndInputPrivacyActivity
import com.openobserve.android.sdk.rules.SessionReplayTestRule
import com.openobserve.android.sdk.utils.SR_TEXT_AND_INPUT_PRIVACY
import com.openobserve.android.sessionreplay.TextAndInputPrivacy
import org.junit.Rule
import org.junit.Test

internal class SrTextAndInputMaskAllInputsTest :
    TextAndInputPrivacyTestBase<SessionReplayTextAndInputPrivacyActivity>() {

    @get:Rule
    override val rule = SessionReplayTestRule(
        SessionReplayTextAndInputPrivacyActivity::class.java,
        trackingConsent = TrackingConsent.GRANTED,
        keepRequests = true,
        intentExtras = mapOf(
            SR_TEXT_AND_INPUT_PRIVACY to TextAndInputPrivacy.MASK_ALL_INPUTS
        )
    )

    @Test
    fun assessMaskAllInputsPayload() {
        assertStaticTextVisible(rule, "Default Text View")
        assertInputTextMaskedWithFixedMask(rule)
    }
}
