/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark.sample.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import coil.Coil
import coil.ImageLoader
import com.openobserve.benchmark.sample.BenchmarkConfigHolder
import com.openobserve.benchmark.sample.OpenObserveFeaturesInitializer
import com.openobserve.benchmark.sample.activities.scenarios.DefaultScenarioActivity
import com.openobserve.benchmark.sample.activities.scenarios.RumAutoScenarioActivity
import com.openobserve.benchmark.sample.activities.scenarios.SessionReplayComposeScenarioActivity
import com.openobserve.benchmark.sample.activities.scenarios.SessionReplayScenarioActivity
import com.openobserve.benchmark.sample.benchmarkAppComponent
import com.openobserve.benchmark.sample.config.BenchmarkConfig
import com.openobserve.benchmark.sample.config.SyntheticsScenario
import okhttp3.OkHttpClient
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
internal class LaunchActivity : AppCompatActivity() {
    @Inject
    internal lateinit var benchmarkConfigHolder: BenchmarkConfigHolder

    @Inject
    internal lateinit var benchmarkFeaturesInitializer: OpenObserveFeaturesInitializer

    @Inject
    internal lateinit var okHttpClient: dagger.Lazy<OkHttpClient>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        application.benchmarkAppComponent.inject(this)

        val config = BenchmarkConfig.resolveSyntheticsBundle(intent.extras)
        benchmarkConfigHolder.config = config

        /**
         * The general recommendation is to initialize OpenObserve SDK at the Application.onCreate
         * to have all the observability as early as possible. However in the Benchmark app we know what kind of run we
         * have [SyntheticsRun.Instrumented] or [SyntheticsRun.Baseline] only in [LaunchActivity.onCreate].
         * It is derived from intent extras.
         */
        benchmarkFeaturesInitializer.initialize(config = config, intent = intent)

        initializeCoil()

        openScenarioActivity(config)

        finish()
    }

    private fun initializeCoil() {
        Coil.setImageLoader(ImageLoader(applicationContext).newBuilder().okHttpClient(okHttpClient.get()).build())
    }

    private fun openScenarioActivity(config: BenchmarkConfig) {
        val activity = when (config.scenario) {
            SyntheticsScenario.RumAuto -> RumAutoScenarioActivity::class.java
            SyntheticsScenario.SessionReplayCompose -> SessionReplayComposeScenarioActivity::class.java
            SyntheticsScenario.SessionReplay -> SessionReplayScenarioActivity::class.java
            else -> DefaultScenarioActivity::class.java
        }

        val intent = Intent(this, activity)
        startActivity(intent)
    }
}
