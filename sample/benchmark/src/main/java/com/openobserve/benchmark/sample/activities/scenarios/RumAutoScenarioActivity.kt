/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark.sample.activities.scenarios

import android.app.Activity
import android.os.Bundle
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.findNavController
import com.openobserve.benchmark.sample.config.BenchmarkConfig
import com.openobserve.benchmark.sample.di.activity.BenchmarkActivityComponent
import com.openobserve.benchmark.sample.navigation.NavigationGraphInitializer
import com.openobserve.benchmark.sample.ui.rumauto.RumAutoBottomNavBar
import com.openobserve.benchmark.sample.ui.rumauto.RumAutoScenarioNavigator
import com.openobserve.sample.benchmark.R
import com.openobserve.sample.benchmark.databinding.FragmentRumAutoHostBinding
import javax.inject.Inject

internal class RumAutoScenarioActivity : BaseScenarioActivity() {
    @Inject
    internal lateinit var rumAutoScenarioNavigator: RumAutoScenarioNavigator

    @Inject
    internal lateinit var navigationGraphInitializer: NavigationGraphInitializer

    @Inject
    internal lateinit var benchmarkConfig: BenchmarkConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        benchmarkActivityComponent.inject(this)

        val binding = FragmentRumAutoHostBinding.inflate(layoutInflater)

        supportActionBar?.hide()
        setContentView(binding.root)

        binding.rumAutoBottomNavbar.setContent {
            val currentTab by rumAutoScenarioNavigator.currentTab.collectAsStateWithLifecycle(null)

            currentTab?.let { tab ->
                RumAutoBottomNavBar(tab) {
                    rumAutoScenarioNavigator.openTab(it)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val navController = findNavController(R.id.nav_host_fragment)
        navigationGraphInitializer.initialize(navController, benchmarkConfig.scenario)

        rumAutoScenarioNavigator.setNavController(findNavController(R.id.nav_host_fragment))
    }
}

internal val Activity.benchmarkActivityComponent: BenchmarkActivityComponent
    get() = (this as BaseScenarioActivity).viewModel.component
