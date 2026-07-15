/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.wear.sample

import android.app.Application
import android.util.Log
import com.openobserve.android.OpenObserve
import com.openobserve.android.OpenObserveSite
import com.openobserve.android.core.configuration.Configuration
import com.openobserve.android.log.Logs
import com.openobserve.android.log.LogsConfiguration
import com.openobserve.android.privacy.TrackingConsent
import com.openobserve.android.rum.Rum
import com.openobserve.android.rum.RumConfiguration
import com.openobserve.android.rum.tracking.ActivityViewTrackingStrategy
import com.openobserve.android.trace.OpenObserveTracing
import com.openobserve.android.trace.GlobalOpenObserveTracer
import com.openobserve.android.trace.Trace
import com.openobserve.android.trace.TraceConfiguration
import com.openobserve.android.trace.opentelemetry.OpenObserveOpenTelemetry
import io.opentelemetry.api.GlobalOpenTelemetry
import timber.log.Timber

/**
 * The main [Application] for the sample WearOs project.
 */
class WearApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeOpenObserve()
    }

    @Suppress("MagicNumber")
    private fun initializeOpenObserve() {
        OpenObserve.setVerbosity(Log.VERBOSE)

        val sdkCore = OpenObserve.initialize(
            this,
            createOpenObserveConfiguration(),
            TrackingConsent.GRANTED
        )

        Rum.enable(
            RumConfiguration.Builder(BuildConfig.DD_RUM_APPLICATION_ID)
                .setTelemetrySampleRate(100f)
                .useViewTrackingStrategy(ActivityViewTrackingStrategy(true))
                .trackUserInteractions()
                .trackLongTasks(250L)
                .apply {
                    if (BuildConfig.DD_OVERRIDE_RUM_URL.isNotBlank()) {
                        useCustomEndpoint(BuildConfig.DD_OVERRIDE_RUM_URL)
                    }
                }
                .build()
        )

        Logs.enable(
            LogsConfiguration.Builder()
                .apply {
                    if (BuildConfig.DD_OVERRIDE_LOGS_URL.isNotBlank()) {
                        useCustomEndpoint(BuildConfig.DD_OVERRIDE_LOGS_URL)
                    }
                }
                .build()
        )

        Trace.enable(
            TraceConfiguration.Builder()
                .apply {
                    if (BuildConfig.DD_OVERRIDE_TRACES_URL.isNotBlank()) {
                        useCustomEndpoint(BuildConfig.DD_OVERRIDE_TRACES_URL)
                    }
                }
                .build()
        )

        OpenObserve.setUserInfo(
            id = "wear 42",
            name = null,
            email = null
        )

        GlobalOpenObserveTracer.registerIfAbsent(
            OpenObserveTracing.newTracerBuilder(checkNotNull(sdkCore))
                .withServiceName(BuildConfig.APPLICATION_ID)
                .build()
        )

        GlobalOpenTelemetry.set(
            OpenObserveOpenTelemetry(BuildConfig.APPLICATION_ID)
        )
    }

    private fun createOpenObserveConfiguration(): Configuration {
        val configBuilder = Configuration.Builder(
            clientToken = BuildConfig.DD_CLIENT_TOKEN,
            env = BuildConfig.BUILD_TYPE,
            variant = BuildConfig.FLAVOR
        )

        try {
            configBuilder.useSite(OpenObserveSite.valueOf(BuildConfig.DD_SITE_NAME))
        } catch (e: IllegalArgumentException) {
            Timber.e("Error setting site to ${BuildConfig.DD_SITE_NAME}")
        }

        return configBuilder.build()
    }
}
