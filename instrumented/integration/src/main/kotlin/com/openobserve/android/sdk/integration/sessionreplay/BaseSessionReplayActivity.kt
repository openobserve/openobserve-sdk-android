/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sdk.integration.sessionreplay

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.openobserve.android.OpenObserve
import com.openobserve.android.rum.Rum
import com.openobserve.android.rum.tracking.ActivityViewTrackingStrategy
import com.openobserve.android.sdk.integration.RuntimeConfig
import com.openobserve.android.sdk.utils.getForgeSeed
import com.openobserve.android.sdk.utils.getSessionReplayPrivacy
import com.openobserve.android.sdk.utils.getSrSampleRate
import com.openobserve.android.sdk.utils.getTrackingConsent
import com.openobserve.android.sessionreplay.SessionReplay
import com.openobserve.android.sessionreplay.SessionReplayConfiguration
import com.openobserve.android.sessionreplay.SessionReplayPrivacy
import java.util.Random

internal abstract class BaseSessionReplayActivity : AppCompatActivity() {
    @Suppress("CheckInternal")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = RuntimeConfig.configBuilder().build()
        val trackingConsent = intent.getTrackingConsent()
        val sessionReplayPrivacy = intent.getSessionReplayPrivacy()
        val sessionReplaySampleRate = intent.getSrSampleRate()
        OpenObserve.setVerbosity(Log.VERBOSE)
        // make sure the previous instance is stopped
        OpenObserve.stopInstance()
        val sdkCore = OpenObserve.initialize(this, config, trackingConsent)
        checkNotNull(sdkCore)
        val featureActivations = mutableListOf(
            {
                val rumConfig = RuntimeConfig.rumConfigBuilder()
                    .trackUserInteractions()
                    .trackLongTasks(RuntimeConfig.LONG_TASK_LARGE_THRESHOLD)
                    .useViewTrackingStrategy(ActivityViewTrackingStrategy(true))
                    .build()
                Rum.enable(rumConfig, sdkCore)
            },
            {
                val sessionReplayConfig = sessionReplayConfiguration(
                    sessionReplayPrivacy,
                    sessionReplaySampleRate
                )
                SessionReplay.enable(sessionReplayConfig, sdkCore)
            }
        )
        featureActivations.shuffled(Random(intent.getForgeSeed()))
            .forEach { it() }
        supportActionBar?.hide()
    }

    @Suppress("DEPRECATION")
    open fun sessionReplayConfiguration(privacy: SessionReplayPrivacy, sampleRate: Float): SessionReplayConfiguration {
        return RuntimeConfig.sessionReplayConfigBuilder(sampleRate)
            .setPrivacy(privacy)
            .build()
    }
}
