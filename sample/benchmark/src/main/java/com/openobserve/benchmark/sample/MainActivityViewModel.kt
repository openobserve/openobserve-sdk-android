/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark.sample

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.openobserve.benchmark.sample.di.activity.BenchmarkActivityComponent
import com.openobserve.benchmark.sample.di.activity.DaggerBenchmarkActivityComponent

internal class MainActivityViewModel(
    val component: BenchmarkActivityComponent
) : ViewModel()

internal class MainActivityViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val benchmarkActivityComponent = DaggerBenchmarkActivityComponent.factory().create(
            deps = application.benchmarkAppComponent
        )

        return MainActivityViewModel(benchmarkActivityComponent) as T
    }
}
