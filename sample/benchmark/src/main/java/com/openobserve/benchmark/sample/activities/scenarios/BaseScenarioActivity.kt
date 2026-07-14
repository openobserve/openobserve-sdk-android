/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark.sample.activities.scenarios

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.openobserve.benchmark.OpenObserveBaseMeter
import com.openobserve.benchmark.sample.MainActivityViewModel
import com.openobserve.benchmark.sample.MainActivityViewModelFactory
import javax.inject.Inject

internal open class BaseScenarioActivity : AppCompatActivity() {
    internal lateinit var viewModel: MainActivityViewModel

    @Inject
    internal lateinit var ooMeter: OpenObserveBaseMeter

    override fun onStart() {
        super.onStart()
        ooMeter.startMeasuring()
    }

    override fun onStop() {
        ooMeter.stopMeasuring()
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = MainActivityViewModelFactory(application)

        viewModel = ViewModelProvider(this, factory)[MainActivityViewModel::class]
    }
}
