/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sdk.rules

import android.app.Activity
import android.content.Intent
import com.openobserve.android.privacy.TrackingConsent
import com.openobserve.android.rum.tracking.ActivityViewTrackingStrategy
import com.openobserve.android.sdk.utils.addExtras

internal open class RumMockServerActivityTestRule<T : Activity>(
    activityClass: Class<T>,
    keepRequests: Boolean = false,
    trackingConsent: TrackingConsent = TrackingConsent.PENDING,
    private val intentExtras: Map<String, Any?> = emptyMap()
) : LifecycleCallbackTestRule<T>(activityClass, keepRequests, trackingConsent) {

    // region ActivityTestRule

    override fun beforeActivityLaunched() {
        removeCallbacks(listOf(ActivityViewTrackingStrategy::class.java))
        super.beforeActivityLaunched()
    }

    override fun afterActivityFinished() {
        removeCallbacks(listOf(ActivityViewTrackingStrategy::class.java))
        super.afterActivityFinished()
    }

    override fun getActivityIntent(): Intent {
        return super.getActivityIntent().apply { addExtras(intentExtras) }
    }

    // endregion
}
