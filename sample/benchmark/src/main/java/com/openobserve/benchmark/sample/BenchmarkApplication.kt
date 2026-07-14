/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark.sample

import android.app.Application
import android.content.Context
import com.openobserve.benchmark.sample.di.app.BenchmarkAppComponent
import com.openobserve.benchmark.sample.di.app.DaggerBenchmarkAppComponent

internal class BenchmarkApplication : Application() {

    internal lateinit var benchmarkAppComponent: BenchmarkAppComponent

    override fun onCreate() {
        super.onCreate()

        benchmarkAppComponent = DaggerBenchmarkAppComponent.factory().create(this)
        benchmarkAppComponent.inject(this)
    }
}

internal val Context.benchmarkAppComponent: BenchmarkAppComponent
    get() = (applicationContext as BenchmarkApplication).benchmarkAppComponent
