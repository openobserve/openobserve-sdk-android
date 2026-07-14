/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sdk.integration.sessionreplay.privacy.legacy.touch

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import com.openobserve.android.privacy.TrackingConsent
import com.openobserve.android.sdk.integration.R
import com.openobserve.android.sdk.integration.RuntimeConfig
import com.openobserve.android.sdk.integration.sessionreplay.BaseSessionReplayTest
import com.openobserve.android.sdk.integration.sessionreplay.INITIAL_WAIT_MS
import com.openobserve.android.sdk.integration.sessionreplay.SessionReplaySegmentUtils.extractSrSegmentAsJson
import com.openobserve.android.sdk.integration.sessionreplay.SessionReplayTouchActivity
import com.openobserve.android.sdk.integration.sessionreplay.UI_THREAD_DELAY_MS
import com.openobserve.android.sdk.rules.HandledRequest
import com.openobserve.android.sdk.rules.SessionReplayTestRule
import com.openobserve.android.sdk.utils.SR_PRIVACY_LEVEL
import com.openobserve.android.sdk.utils.waitFor
import com.openobserve.android.sessionreplay.RECORD_TYPE_INCREMENTAL_SNAPSHOT
import com.openobserve.android.sessionreplay.SessionReplayPrivacy
import com.openobserve.tools.unit.ConditionWatcher
import com.google.gson.JsonObject
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test

internal class SrTouchAllowTest : BaseSessionReplayTest<SessionReplayTouchActivity>() {

    @get:Rule
    override val rule = SessionReplayTestRule(
        SessionReplayTouchActivity::class.java,
        trackingConsent = TrackingConsent.GRANTED,
        keepRequests = true,
        intentExtras = mapOf(SR_PRIVACY_LEVEL to SessionReplayPrivacy.ALLOW)
    )

    @Test
    fun assessTouchRecordsWithAllow() {
        runTouchScenario()

        ConditionWatcher {
            val requests = rule.getRequests(RuntimeConfig.sessionReplayEndpointUrl)
            val records = extractRecordsFromRequests(requests)

            assertRecordStructure(records)

            val touchRecords = extractTouchRecordsFromRequests(requests)

            assertThat(touchRecords)
                .describedAs("Should capture touch interaction records with ALLOW privacy")
                .isNotEmpty

            touchRecords.forEach { record ->
                val data = record.get("data")?.asJsonObject
                requireNotNull(data)

                val pointerType = data.get("pointerType")?.asString
                val x = data.get("x")?.asLong
                val y = data.get("y")?.asLong

                requireNotNull(x)
                requireNotNull(y)

                assertThat(x).isGreaterThan(0)
                assertThat(y).isGreaterThan(0)

                assertThat(pointerType)
                    .describedAs("Pointer type should be TOUCH")
                    .isEqualTo("touch")
            }

            true
        }.doWait(timeoutMs = INITIAL_WAIT_MS)
    }

    private fun runTouchScenario() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.waitForIdleSync()

        Espresso.onView(ViewMatchers.withId(R.id.button1))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.isRoot()).perform(waitFor(UI_THREAD_DELAY_MS))
        instrumentation.waitForIdleSync()
    }

    private fun extractTouchRecordsFromRequests(requests: List<HandledRequest>): List<JsonObject> {
        return requests
            .mapNotNull { it.extractSrSegmentAsJson()?.asJsonObject }
            .flatMap { segment ->
                segment.getAsJsonArray("records")
                    ?.filter { record ->
                        val recordObj = record.asJsonObject
                        val type = recordObj.get("type")?.asString
                        type == RECORD_TYPE_INCREMENTAL_SNAPSHOT.toString()
                    }
                    ?.filter { record ->
                        val data = record.asJsonObject.get("data")?.asJsonObject
                        data?.has("pointerType") == true
                    }
                    ?.map { it.asJsonObject }
                    ?: emptyList()
            }
    }
}
