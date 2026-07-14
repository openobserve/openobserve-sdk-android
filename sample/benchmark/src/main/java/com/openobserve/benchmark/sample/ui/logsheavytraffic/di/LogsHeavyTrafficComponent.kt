/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark.sample.ui.logsheavytraffic.di

import com.openobserve.android.log.Logger
import com.openobserve.benchmark.sample.di.common.DispatchersModule
import com.openobserve.benchmark.sample.ui.logsheavytraffic.LogsHeavyTrafficFragment
import com.openobserve.benchmark.sample.ui.logsheavytraffic.LogsHeavyTrafficHostFragment
import com.openobserve.benchmark.sample.ui.logsheavytraffic.LogsHeavyTrafficSettingsFragment
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.CoroutineScope
import javax.inject.Scope

internal interface LogsHeavyTrafficComponentDependencies {
    val logger: Logger
}

@Scope
internal annotation class LogsHeavyTrafficScope

@LogsHeavyTrafficScope
@Component(
    dependencies = [LogsHeavyTrafficComponentDependencies::class],
    modules = [
        DispatchersModule::class
    ]
)
internal interface LogsHeavyTrafficComponent {
    @Component.Factory
    interface Factory {
        fun create(
            deps: LogsHeavyTrafficComponentDependencies,
            @BindsInstance viewModelScope: CoroutineScope
        ): LogsHeavyTrafficComponent
    }

    fun inject(logsHeavyTrafficHostFragment: LogsHeavyTrafficHostFragment)
    fun inject(logsHeavyTrafficFragment: LogsHeavyTrafficFragment)
    fun inject(logsHeavyTrafficSettingsFragment: LogsHeavyTrafficSettingsFragment)
}
