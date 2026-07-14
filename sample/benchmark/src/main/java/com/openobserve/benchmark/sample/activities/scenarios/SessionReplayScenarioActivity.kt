/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark.sample.activities.scenarios

import android.os.Bundle
import androidx.navigation.findNavController
import com.openobserve.benchmark.sample.config.BenchmarkConfig
import com.openobserve.benchmark.sample.navigation.NavigationGraphInitializer
import com.openobserve.benchmark.sample.ui.sessionreplay.SessionReplayNavigationManager
import com.openobserve.sample.benchmark.R
import javax.inject.Inject

internal class SessionReplayScenarioActivity : BaseScenarioActivity() {
    @Inject
    internal lateinit var sessionReplayNavigationManager: SessionReplayNavigationManager

    @Inject
    internal lateinit var config: BenchmarkConfig

    @Inject
    internal lateinit var navigationGraphInitializer: NavigationGraphInitializer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        benchmarkActivityComponent.inject(this)

        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        val navController = findNavController(R.id.nav_host_fragment)

        navigationGraphInitializer.initialize(navController, config.scenario)
        sessionReplayNavigationManager.setNavController(navController)
    }
}
