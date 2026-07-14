/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

@file:Suppress("MethodOverloading", "TooManyFunctions")

package com.openobserve.benchmark.sample.di.activity

import android.content.Context
import com.openobserve.android.api.SdkCore
import com.openobserve.android.log.Logger
import com.openobserve.android.rum.RumMonitor
import com.openobserve.benchmark.OpenObserveBaseMeter
import com.openobserve.benchmark.sample.activities.scenarios.DefaultScenarioActivity
import com.openobserve.benchmark.sample.activities.scenarios.RumAutoScenarioActivity
import com.openobserve.benchmark.sample.activities.scenarios.SessionReplayComposeScenarioActivity
import com.openobserve.benchmark.sample.activities.scenarios.SessionReplayScenarioActivity
import com.openobserve.benchmark.sample.config.BenchmarkConfig
import com.openobserve.benchmark.sample.di.common.DispatchersModule
import com.openobserve.benchmark.sample.network.rickandmorty.RickAndMortyNetworkService
import com.openobserve.benchmark.sample.ui.logscustom.LogsFragment
import com.openobserve.benchmark.sample.ui.logsheavytraffic.di.LogsHeavyTrafficComponentDependencies
import com.openobserve.benchmark.sample.ui.rumauto.screens.characterdetails.RumAutoCharacterDetailFragment
import com.openobserve.benchmark.sample.ui.rumauto.screens.characters.RumAutoCharactersFragment
import com.openobserve.benchmark.sample.ui.rumauto.screens.episodedetails.di.RumAutoEpisodeDetailsComponentDependencies
import com.openobserve.benchmark.sample.ui.rumauto.screens.episodes.RumAutoEpisodesListFragment
import com.openobserve.benchmark.sample.ui.rumauto.screens.locationdetails.di.RumAutoLocationDetailsComponentDependencies
import com.openobserve.benchmark.sample.ui.rumauto.screens.locations.di.RumAutoLocationsComponentDependencies
import com.openobserve.benchmark.sample.ui.rummanual.RumManualScenarioFragment
import com.openobserve.benchmark.sample.ui.sessionreplay.SessionReplayAppcompatFragment
import com.openobserve.benchmark.sample.ui.sessionreplay.SessionReplayMaterialFragment
import com.openobserve.benchmark.sample.ui.trace.TraceScenarioFragment
import dagger.Component
import io.opentelemetry.api.trace.Tracer
import javax.inject.Scope

@Scope
internal annotation class BenchmarkActivityScope

internal interface BenchmarkActivityComponentDependencies {
    val context: Context
    val benchmarkConfig: BenchmarkConfig

    val sdkCore: SdkCore
    val logger: Logger
    val rumMonitor: RumMonitor
    val ooBaseMeter: OpenObserveBaseMeter
    val tracer: Tracer

    val rickAndMortyNetworkService: RickAndMortyNetworkService
}

@Component(
    dependencies = [
        BenchmarkActivityComponentDependencies::class
    ],
    modules = [
        BenchmarkActivityModule::class,
        ViewModelsModule::class,
        BenchmarkActivityModule::class,
        DispatchersModule::class
    ]
)
@BenchmarkActivityScope
internal interface BenchmarkActivityComponent :
    LogsHeavyTrafficComponentDependencies,
    RumAutoEpisodeDetailsComponentDependencies,
    RumAutoLocationsComponentDependencies,
    RumAutoLocationDetailsComponentDependencies {
    @Component.Factory
    interface Factory {
        fun create(
            deps: BenchmarkActivityComponentDependencies
        ): BenchmarkActivityComponent
    }

    fun inject(defaultScenarioActivity: DefaultScenarioActivity)
    fun inject(rumAutoScenarioActivity: RumAutoScenarioActivity)
    fun inject(sessionReplayAppcompatActivity: SessionReplayComposeScenarioActivity)
    fun inject(sessionReplayScenarioActivity: SessionReplayScenarioActivity)

    fun inject(sessionReplayAppcompatFragment: SessionReplayAppcompatFragment)
    fun inject(sessionReplayMaterialFragment: SessionReplayMaterialFragment)
    fun inject(logsFragment: LogsFragment)
    fun inject(traceScenarioFragment: TraceScenarioFragment)
    fun inject(rumManualScenarioFragment: RumManualScenarioFragment)

    fun inject(rumAutoCharactersFragment: RumAutoCharactersFragment)
    fun inject(rumAutoCharacterDetailFragment: RumAutoCharacterDetailFragment)
    fun inject(rumAutoEpisodesListFragment: RumAutoEpisodesListFragment)
}
