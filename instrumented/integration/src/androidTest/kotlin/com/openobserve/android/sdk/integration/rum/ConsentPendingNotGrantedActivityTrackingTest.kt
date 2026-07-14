/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sdk.integration.rum

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.openobserve.android.OpenObserve
import com.openobserve.android.privacy.TrackingConsent
import com.openobserve.android.sdk.integration.RuntimeConfig
import com.openobserve.android.sdk.rules.RumMockServerActivityTestRule
import com.openobserve.tools.unit.ConditionWatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
internal class ConsentPendingNotGrantedActivityTrackingTest : ActivityTrackingTest() {

    @get:Rule
    val mockServerRule = RumMockServerActivityTestRule(
        ActivityTrackingPlaygroundActivity::class.java,
        keepRequests = true,
        intentExtras = expectedViewArguments,
        trackingConsent = TrackingConsent.PENDING
    )

    @Test
    fun verifyAllRumEventsAreDropped() {
        runInstrumentationScenario(mockServerRule)

        // update the tracking consent
        OpenObserve.setTrackingConsent(TrackingConsent.NOT_GRANTED)

        // Wait to make sure all batches are consumed
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        ConditionWatcher {
            verifyNoRumPayloadSent(mockServerRule.getRequests(RuntimeConfig.rumEndpointUrl))
            true
        }.doWait(timeoutMs = FINAL_WAIT_MS)
    }
}
