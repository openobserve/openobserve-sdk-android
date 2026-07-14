/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark.sample.di.app

import com.openobserve.benchmark.sample.BenchmarkApplication
import com.openobserve.benchmark.sample.BenchmarkGlideModule
import com.openobserve.benchmark.sample.activities.LaunchActivity
import com.openobserve.benchmark.sample.di.activity.BenchmarkActivityComponentDependencies
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [
        AppModule::class,
        OpenObserveModule::class,
        NetworkModule::class,
        OpenTelemetryModule::class
    ]
)
@Singleton
internal interface BenchmarkAppComponent : BenchmarkActivityComponentDependencies {
    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: BenchmarkApplication
        ): BenchmarkAppComponent
    }

    fun inject(benchmarkApplication: BenchmarkApplication)
    fun inject(launchActivity: LaunchActivity)
    fun inject(glideModule: BenchmarkGlideModule)
}
