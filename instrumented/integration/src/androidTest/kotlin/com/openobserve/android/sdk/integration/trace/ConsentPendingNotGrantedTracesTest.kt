/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.sdk.integration.trace

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.openobserve.android.OpenObserve
import com.openobserve.android.privacy.TrackingConsent
import com.openobserve.android.sdk.integration.RuntimeConfig
import com.openobserve.android.sdk.rules.MockServerActivityTestRule
import com.openobserve.tools.unit.ConditionWatcher
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
internal class ConsentPendingNotGrantedTracesTest : TracesTest() {

    @get:Rule
    val mockServerRule = MockServerActivityTestRule(
        ActivityLifecycleTrace::class.java,
        keepRequests = true,
        trackingConsent = TrackingConsent.PENDING
    )

    @Test
    fun verifyAllTracesAreDropped() {
        runInstrumentationScenario(mockServerRule)

        // update the tracking consent
        OpenObserve.setTrackingConsent(TrackingConsent.NOT_GRANTED)

        // Wait to make sure all batches are consumed
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        ConditionWatcher {
            val tracePayloads = mockServerRule.getRequests(RuntimeConfig.tracesEndpointUrl)
            assertThat(tracePayloads).isEmpty()
            true
        }.doWait(timeoutMs = INITIAL_WAIT_MS)
    }
}
