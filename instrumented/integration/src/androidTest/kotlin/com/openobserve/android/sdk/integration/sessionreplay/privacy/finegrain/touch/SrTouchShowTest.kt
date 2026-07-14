/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sdk.integration.sessionreplay.privacy.finegrain.touch

import com.openobserve.android.privacy.TrackingConsent
import com.openobserve.android.sdk.integration.sessionreplay.SessionReplayTouchActivity
import com.openobserve.android.sdk.rules.SessionReplayTestRule
import com.openobserve.android.sdk.utils.SR_TOUCH_PRIVACY
import com.openobserve.android.sessionreplay.TouchPrivacy
import org.junit.Rule
import org.junit.Test

internal class SrTouchShowTest : TouchPrivacyTestBase<SessionReplayTouchActivity>() {

    @get:Rule
    override val rule = SessionReplayTestRule(
        SessionReplayTouchActivity::class.java,
        trackingConsent = TrackingConsent.GRANTED,
        keepRequests = true,
        intentExtras = mapOf(
            SR_TOUCH_PRIVACY to TouchPrivacy.SHOW
        )
    )

    @Test
    fun assessTouchRecordsWithShow() {
        runTouchScenario()
        assertTouchRecordsExist(rule)
    }
}
