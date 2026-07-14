/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark.sample.ui.rumauto.screens.episodedetails.di

import com.openobserve.benchmark.sample.di.common.DispatchersModule
import com.openobserve.benchmark.sample.network.rickandmorty.RickAndMortyNetworkService
import com.openobserve.benchmark.sample.network.rickandmorty.models.Episode
import com.openobserve.benchmark.sample.ui.rumauto.RumAutoScenarioNavigator
import com.openobserve.benchmark.sample.ui.rumauto.screens.episodedetails.RumAutoEpisodeDetailsFragment
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.CoroutineScope
import javax.inject.Scope

internal interface RumAutoEpisodeDetailsComponentDependencies {
    val rickAndMortyNetworkService: RickAndMortyNetworkService
    val rumAutoScenarioNavigator: RumAutoScenarioNavigator
}

@Scope
internal annotation class RumAutoEpisodeDetailsScope

@Component(
    dependencies = [
        RumAutoEpisodeDetailsComponentDependencies::class
    ],
    modules = [
        DispatchersModule::class
    ]
)
@RumAutoEpisodeDetailsScope
internal interface RumAutoEpisodeDetailsComponent {
    @Component.Factory
    interface Factory {
        fun create(
            deps: RumAutoEpisodeDetailsComponentDependencies,
            @BindsInstance viewModelScope: CoroutineScope,
            @BindsInstance episode: Episode
        ): RumAutoEpisodeDetailsComponent
    }

    fun inject(fragment: RumAutoEpisodeDetailsFragment)
}
