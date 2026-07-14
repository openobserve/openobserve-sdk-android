/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark.sample.ui.logsheavytraffic

import androidx.navigation.NavController
import androidx.navigation.createGraph
import androidx.navigation.fragment.fragment
import com.openobserve.benchmark.sample.ui.logsheavytraffic.di.LogsHeavyTrafficScope
import javax.inject.Inject

@LogsHeavyTrafficScope
internal class LogsHeavyTrafficNavigationManager @Inject constructor() {
    private var navController: NavController? = null

    fun setNavController(navController: NavController) {
        this.navController = navController

        navController.graph = navController.createGraph(startDestination = LOGS_HEAVY_TRAFFIC_KEY) {
            fragment<LogsHeavyTrafficFragment>(LOGS_HEAVY_TRAFFIC_KEY)
            fragment<LogsHeavyTrafficSettingsFragment>(LOGS_HEAVY_TRAFFIC_SETTINGS_KEY)
        }
    }

    fun openSettings() {
        navController?.navigate(LOGS_HEAVY_TRAFFIC_SETTINGS_KEY)
    }

    fun closeSettings() {
        navController?.popBackStack()
    }
}

private const val LOGS_HEAVY_TRAFFIC_KEY = "logs_heavy_traffic"
private const val LOGS_HEAVY_TRAFFIC_SETTINGS_KEY = "logs_heavy_traffic_settings"
