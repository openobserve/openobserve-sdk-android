/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark.sample.di.activity

import com.openobserve.benchmark.sample.ui.sessionreplay.SessionReplayNavigationManager
import com.openobserve.benchmark.sample.ui.sessionreplay.SessionReplayNavigationManagerImpl
import dagger.Binds
import dagger.Module

@Module
internal interface BenchmarkActivityModule {
    @Binds
    @BenchmarkActivityScope
    fun bindSessionReplayNavigationManager(impl: SessionReplayNavigationManagerImpl): SessionReplayNavigationManager
}
