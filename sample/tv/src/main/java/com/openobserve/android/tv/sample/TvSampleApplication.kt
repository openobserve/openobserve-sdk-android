/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.tv.sample

import android.app.Application
import android.util.Log
import com.openobserve.android.OpenObserve
import com.openobserve.android.OpenObserveSite
import com.openobserve.android.core.configuration.BatchSize
import com.openobserve.android.core.configuration.Configuration
import com.openobserve.android.core.configuration.UploadFrequency
import com.openobserve.android.core.sampling.RateBasedSampler
import com.openobserve.android.log.Logger
import com.openobserve.android.log.Logs
import com.openobserve.android.log.LogsConfiguration
import com.openobserve.android.okhttp.OpenObserveEventListener
import com.openobserve.android.okhttp.OpenObserveInterceptor
import com.openobserve.android.okhttp.trace.TracingInterceptor
import com.openobserve.android.privacy.TrackingConsent
import com.openobserve.android.rum.GlobalRumMonitor
import com.openobserve.android.rum.Rum
import com.openobserve.android.rum.RumConfiguration
import com.openobserve.android.rum.tracking.ActivityViewTrackingStrategy
import com.openobserve.android.sessionreplay.ImagePrivacy
import com.openobserve.android.sessionreplay.SessionReplay
import com.openobserve.android.sessionreplay.SessionReplayConfiguration
import com.openobserve.android.sessionreplay.SystemRequirementsConfiguration
import com.openobserve.android.sessionreplay.TextAndInputPrivacy
import com.openobserve.android.sessionreplay.TouchPrivacy
import com.openobserve.android.sessionreplay.material.MaterialExtensionSupport
import com.openobserve.android.timber.OpenObserveTree
import com.openobserve.android.tv.sample.net.OkHttpDownloader
import okhttp3.OkHttpClient
import org.schabi.newpipe.extractor.NewPipe
import timber.log.Timber

/**
 * The main [Application] for the sample TV project.
 */
class TvSampleApplication : Application() {

    internal lateinit var okHttpClient: OkHttpClient

    override fun onCreate() {
        super.onCreate()
        initializeOpenObserve()
        initializeTimber()
        initializeOkHttp()
        initializeNewPipe()
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

        val sessionReplayConfig = createSessionReplayConfiguration()
        SessionReplay.enable(sessionReplayConfig)

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

    private fun createSessionReplayConfiguration(): SessionReplayConfiguration {
        return SessionReplayConfiguration.Builder(FULL_SAMPLING_RATE)
            .setImagePrivacy(ImagePrivacy.MASK_ALL)
            .setTouchPrivacy(TouchPrivacy.SHOW)
            .setTextAndInputPrivacy(TextAndInputPrivacy.MASK_SENSITIVE_INPUTS)
            .addExtensionSupport(MaterialExtensionSupport())
            .setSystemRequirements(SystemRequirementsConfiguration.NONE)
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

    @Suppress("TooGenericExceptionCaught", "CheckInternal")
    private fun initializeTimber() {
        val logger = Logger.Builder()
            .setName("timber")
            .setNetworkInfoEnabled(true)
            .setLogcatLogsEnabled(true)
            .build()

        Timber.plant(OpenObserveTree(logger))
    }

    private fun initializeOkHttp() {
        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(
                OpenObserveInterceptor.Builder(emptyMap())
                    .setTraceSampler(RateBasedSampler(FULL_SAMPLING_RATE))
                    .build()
            )
            .addNetworkInterceptor(
                TracingInterceptor.Builder(emptyMap())
                    .setTraceSampler(RateBasedSampler(FULL_SAMPLING_RATE))
                    .build()
            )
            .eventListenerFactory(OpenObserveEventListener.Factory())
            .build()
    }

    private fun initializeNewPipe() {
        NewPipe.init(OkHttpDownloader(okHttpClient))
    }

    companion object {
        private const val FULL_SAMPLING_RATE = 100f
    }
}
