/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark.sample.di.app

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.openobserve.android.OpenObserve
import com.openobserve.android.api.SdkCore
import com.openobserve.android.core.configuration.BackPressureMitigation
import com.openobserve.android.core.configuration.BackPressureStrategy
import com.openobserve.android.core.configuration.BatchSize
import com.openobserve.android.core.configuration.Configuration
import com.openobserve.android.core.configuration.UploadFrequency
import com.openobserve.android.log.Logger
import com.openobserve.android.privacy.TrackingConsent
import com.openobserve.android.rum.GlobalRumMonitor
import com.openobserve.android.rum.RumMonitor
import com.openobserve.benchmark.OpenObserveBaseMeter
import com.openobserve.benchmark.OpenObserveExporterConfiguration
import com.openobserve.benchmark.OpenObserveSdkMeter
import com.openobserve.benchmark.OpenObserveVitalsMeter
import com.openobserve.benchmark.sample.config.BenchmarkConfig
import com.openobserve.benchmark.sample.config.SyntheticsRun
import com.openobserve.benchmark.sample.config.SyntheticsScenario
import com.openobserve.sample.benchmark.BuildConfig
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal interface OpenObserveModule {
    companion object {
        @Provides
        @Singleton
        fun provideSdkCore(
            context: Context,
            config: BenchmarkConfig
        ): SdkCore {
            if (config.run == SyntheticsRun.Baseline) {
                return OpenObserve.getInstance() // returns NoOpInternalSdkCore under the hood
            }

            return OpenObserve.initialize(
                context,
                createOpenObserveConfiguration(),
                TrackingConsent.GRANTED
            )!!
        }

        @Provides
        @Singleton
        fun provideOpenObserveMeter(config: BenchmarkConfig, sdkCore: SdkCore): OpenObserveBaseMeter {
            val exporterConfig = OpenObserveExporterConfiguration.Builder(BuildConfig.BENCHMARK_API_KEY)
                .setApplicationId(BuildConfig.APPLICATION_ID)
                .setApplicationName(BENCHMARK_APPLICATION_NAME)
                .setRun(config.getRun())
                .setScenario(config.getScenario())
                .setApplicationVersion(BuildConfig.VERSION_NAME)
                .setIntervalInSeconds(METER_INTERVAL_IN_SECONDS)
                .build()

            return if (config.scenario == SyntheticsScenario.Upload) {
                OpenObserveSdkMeter.create(exporterConfig)
            } else {
                OpenObserveVitalsMeter.create(
                    exporterConfig,
                    sdkCore
                )
            }
        }

        @Provides
        @Singleton
        fun provideLogger(sdkCore: SdkCore): Logger {
            return Logger.Builder(sdkCore)
                .setName("benchmarkLogger")
                .setLogcatLogsEnabled(true)
                .build()
        }

        @Provides
        @Singleton
        fun provideRumMonitor(sdkCore: SdkCore): RumMonitor {
            return GlobalRumMonitor.get(sdkCore = sdkCore)
        }
    }
}

@SuppressLint("LogNotTimber")
private fun createOpenObserveConfiguration(): Configuration {
    val configBuilder = Configuration.Builder(
        clientToken = BuildConfig.BENCHMARK_CLIENT_TOKEN,
        env = BuildConfig.BUILD_TYPE
    )
        .setBatchSize(BatchSize.SMALL)
        .setUploadFrequency(UploadFrequency.FREQUENT)

    configBuilder.setBackpressureStrategy(
        BackPressureStrategy(
            CAPACITY_BACK_PRESSURE_STRATEGY,
            { Log.w("BackPressure", "Threshold reached") },
            { Log.e("BackPressure", "Item dropped: $it") },
            BackPressureMitigation.IGNORE_NEWEST
        )
    )

    return configBuilder.build()
}

// the same as the default one
private const val CAPACITY_BACK_PRESSURE_STRATEGY = 1024

private const val METER_INTERVAL_IN_SECONDS = 10L
private const val BENCHMARK_APPLICATION_NAME = "Benchmark Application"
