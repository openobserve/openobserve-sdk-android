/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark.sample.activities.scenarios

import android.os.Bundle
import androidx.activity.compose.setContent
import com.openobserve.benchmark.sample.ui.sessionreplaycompose.MainView

internal class SessionReplayComposeScenarioActivity : BaseScenarioActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        benchmarkActivityComponent.inject(this)

        supportActionBar?.hide()
        setContent {
            MainView()
        }
    }
}
