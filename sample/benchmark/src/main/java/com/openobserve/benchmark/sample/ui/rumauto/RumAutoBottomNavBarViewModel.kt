/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark.sample.ui.rumauto

import com.openobserve.benchmark.sample.di.activity.BenchmarkActivityScope
import javax.inject.Inject

internal enum class RumAutoScenarioTab {
    CHARACTERS,
    EPISODES,
    LOCATIONS
}

internal sealed interface RumAutoBottomNavBarAction {
    data class OnTabClicked(val tab: RumAutoScenarioTab) : RumAutoBottomNavBarAction
}

@BenchmarkActivityScope
internal class RumAutoBottomNavBarViewModel @Inject constructor(
    private val navigator: RumAutoScenarioNavigator
) {
    fun dispatch(action: RumAutoBottomNavBarAction) {
        when (action) {
            is RumAutoBottomNavBarAction.OnTabClicked -> navigator.openTab(action.tab)
        }
    }
}
