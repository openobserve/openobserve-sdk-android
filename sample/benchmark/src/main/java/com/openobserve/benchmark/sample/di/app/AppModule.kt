/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark.sample.di.app

import android.content.Context
import com.openobserve.benchmark.sample.BenchmarkApplication
import com.openobserve.benchmark.sample.BenchmarkConfigHolder
import com.openobserve.benchmark.sample.config.BenchmarkConfig
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal interface AppModule {
    @Binds
    @Singleton
    fun bindApplicationContext(benchmarkApplication: BenchmarkApplication): Context

    companion object {
        @Provides
        fun provideBenchmarkConfig(
            holder: BenchmarkConfigHolder
        ): BenchmarkConfig {
            return holder.config
        }
    }
}
