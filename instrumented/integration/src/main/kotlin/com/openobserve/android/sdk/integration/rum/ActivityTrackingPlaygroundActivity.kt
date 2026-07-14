/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sdk.integration.rum

import android.app.ActivityManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.openobserve.android.OpenObserve
import com.openobserve.android.rum.DdRumContentProvider
import com.openobserve.android.rum.Rum
import com.openobserve.android.rum.tracking.ActivityViewTrackingStrategy
import com.openobserve.android.sdk.integration.R
import com.openobserve.android.sdk.integration.RuntimeConfig
import com.openobserve.android.sdk.utils.getTrackingConsent

internal class ActivityTrackingPlaygroundActivity : AppCompatActivity() {

    @Suppress("CheckInternal")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // we will use a large long task threshold to make sure we will not have LongTask events
        // noise in our integration tests.
        val config = RuntimeConfig.configBuilder().build()
        val trackingConsent = intent.getTrackingConsent()

        OpenObserve.setVerbosity(Log.VERBOSE)
        val sdkCore = OpenObserve.initialize(this, config, trackingConsent)
        checkNotNull(sdkCore)

        DdRumContentProvider.processImportance = ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND

        val rumConfig = RuntimeConfig.rumConfigBuilder()
            .trackUserInteractions()
            .trackLongTasks(RuntimeConfig.LONG_TASK_LARGE_THRESHOLD)
            .useViewTrackingStrategy(ActivityViewTrackingStrategy(true))
            .build()
        Rum.enable(rumConfig, sdkCore)
        setContentView(R.layout.fragment_tracking_layout)
    }
}
