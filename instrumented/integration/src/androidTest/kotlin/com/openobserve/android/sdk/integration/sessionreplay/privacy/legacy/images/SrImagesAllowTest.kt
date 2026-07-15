/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.sdk.integration.sessionreplay.privacy.legacy.images

import com.openobserve.android.privacy.TrackingConsent
import com.openobserve.android.sdk.integration.RuntimeConfig
import com.openobserve.android.sdk.integration.sessionreplay.BaseSessionReplayTest
import com.openobserve.android.sdk.integration.sessionreplay.INITIAL_WAIT_MS
import com.openobserve.android.sdk.integration.sessionreplay.SessionReplayImagesActivity
import com.openobserve.android.sdk.rules.SessionReplayTestRule
import com.openobserve.android.sdk.utils.SR_PRIVACY_LEVEL
import com.openobserve.android.sessionreplay.SessionReplayPrivacy
import com.openobserve.android.sessionreplay.WIREFRAME_TYPE_IMAGE
import com.openobserve.tools.unit.ConditionWatcher
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test

internal class SrImagesAllowTest :
    BaseSessionReplayTest<SessionReplayImagesActivity>() {

    @get:Rule
    override val rule = SessionReplayTestRule(
        SessionReplayImagesActivity::class.java,
        trackingConsent = TrackingConsent.GRANTED,
        keepRequests = true,
        intentExtras = mapOf(SR_PRIVACY_LEVEL to SessionReplayPrivacy.ALLOW)
    )

    @Test
    fun assessRecordedScreenPayload() {
        ConditionWatcher {
            val requests = rule.getRequests(RuntimeConfig.sessionReplayEndpointUrl)
            val records = extractRecordsFromRequests(requests)

            assertRecordStructure(records)

            val wireframes = extractWireframesFromRequests(requests)

            val imageWireframes = wireframes.filter { wireframe ->
                wireframe.get("type")?.asString == WIREFRAME_TYPE_IMAGE
            }

            assertThat(imageWireframes)
                .describedAs("Should capture images as image wireframes with ALLOW privacy")
                .hasSizeGreaterThanOrEqualTo(2)

            true
        }.doWait(timeoutMs = INITIAL_WAIT_MS)
    }
}
