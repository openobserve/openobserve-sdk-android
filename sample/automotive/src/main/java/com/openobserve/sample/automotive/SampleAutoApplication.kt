/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.sample.automotive

import android.app.Application
import android.util.Log
import com.openobserve.android.OpenObserve
import com.openobserve.android.OpenObserveSite
import com.openobserve.android.core.configuration.BatchSize
import com.openobserve.android.core.configuration.Configuration
import com.openobserve.android.core.configuration.UploadFrequency
import com.openobserve.android.log.Logs
import com.openobserve.android.log.LogsConfiguration
import com.openobserve.android.privacy.TrackingConsent
import com.openobserve.android.rum.GlobalRumMonitor
import com.openobserve.android.rum.Rum
import com.openobserve.android.rum.RumConfiguration
import com.openobserve.android.rum.tracking.ActivityViewTrackingStrategy

@Suppress("UndocumentedPublicClass")
class SampleAutoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeOpenObserve()
    }

    private fun initializeOpenObserve() {
        OpenObserve.setVerbosity(Log.VERBOSE)
        OpenObserve.initialize(
            this,
            createOpenObserveConfiguration(),
            TrackingConsent.GRANTED
        )

        val rumConfig = createRumConfiguration()
        Rum.enable(rumConfig)

        val logsConfig = LogsConfiguration.Builder().build()
        Logs.enable(logsConfig)

        GlobalRumMonitor.get().debug = true
    }

    private fun createRumConfiguration(): RumConfiguration {
        return RumConfiguration.Builder(BuildConfig.DD_RUM_APPLICATION_ID)
            .useViewTrackingStrategy(
                ActivityViewTrackingStrategy(true)
            )
            .setTelemetrySampleRate(FULL_SAMPLING_RATE)
            .trackUserInteractions()
            .build()
    }
    private fun createOpenObserveConfiguration(): Configuration {
        return Configuration.Builder(
            clientToken = BuildConfig.DD_CLIENT_TOKEN,
            env = "test",
            variant = ""
        )
            .useSite(OpenObserveSite.US1)
            .setBatchSize(BatchSize.SMALL)
            .setUploadFrequency(UploadFrequency.FREQUENT)
            .build()
    }

    private companion object {
        private const val FULL_SAMPLING_RATE = 100f
    }
}
