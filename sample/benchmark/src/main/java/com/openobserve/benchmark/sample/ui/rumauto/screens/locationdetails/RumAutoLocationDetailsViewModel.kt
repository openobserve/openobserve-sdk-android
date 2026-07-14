/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark.sample.ui.rumauto.screens.locationdetails

import com.openobserve.benchmark.sample.di.common.CoroutineDispatcherQualifier
import com.openobserve.benchmark.sample.di.common.CoroutineDispatcherType
import com.openobserve.benchmark.sample.network.KtorHttpResponse
import com.openobserve.benchmark.sample.network.rickandmorty.RickAndMortyNetworkService
import com.openobserve.benchmark.sample.network.rickandmorty.models.Character
import com.openobserve.benchmark.sample.network.rickandmorty.models.Location
import com.openobserve.benchmark.sample.ui.rumauto.RumAutoScenarioNavigator
import com.openobserve.benchmark.sample.ui.rumauto.screens.locationdetails.di.RumAutoLocationDetailsScope
import com.openobserve.benchmark.sample.utils.BenchmarkAsyncTask
import com.openobserve.benchmark.sample.utils.StateMachine
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

internal data class RumAutoLocationDetailsState(
    val location: Location,
    val residentsLoadingTask: BenchmarkAsyncTask<KtorHttpResponse<List<Character>>, ResidentsLoadingTask>
) {
    class ResidentsLoadingTask(val ids: List<String>)
}

internal sealed interface RumAutoLocationDetailsAction {
    data class ResidentsLoadingFinished(
        val response: KtorHttpResponse<List<Character>>,
        val task: RumAutoLocationDetailsState.ResidentsLoadingTask
    ) : RumAutoLocationDetailsAction

    data class OnCharacterClicked(val character: Character) : RumAutoLocationDetailsAction
}

@RumAutoLocationDetailsScope
internal class RumAutoLocationDetailsViewModel @Inject constructor(
    private val navigator: RumAutoScenarioNavigator,
    private val location: Location,
    @CoroutineDispatcherQualifier(CoroutineDispatcherType.Default) private val defaultDispatcher: CoroutineDispatcher,
    private val viewModelScope: CoroutineScope,
    private val rickAndMortyNetworkService: RickAndMortyNetworkService
) {

    private val stateMachine = StateMachine.create(
        initialState = RumAutoLocationDetailsState(
            residentsLoadingTask = run {
                val task = RumAutoLocationDetailsState.ResidentsLoadingTask(characterIds(location))
                val job = launchResidentsLoadingTask(task)
                BenchmarkAsyncTask.Loading(job = job, key = task)
            },
            location = location
        ),
        dispatcher = defaultDispatcher,
        scope = viewModelScope,
        processAction = ::processAction
    )

    val state: StateFlow<RumAutoLocationDetailsState> get() = stateMachine.state

    fun dispatch(action: RumAutoLocationDetailsAction) {
        stateMachine.dispatch(action)
    }

    private fun processAction(
        prev: RumAutoLocationDetailsState,
        action: RumAutoLocationDetailsAction
    ): RumAutoLocationDetailsState {
        return when (action) {
            is RumAutoLocationDetailsAction.OnCharacterClicked -> {
                viewModelScope.launch {
                    navigator.openCharacterScreen(action.character)
                }
                prev
            }

            else -> prev.copy(
                residentsLoadingTask = processTask(prev, action)
            )
        }
    }

    private fun processTask(
        prev: RumAutoLocationDetailsState,
        action: RumAutoLocationDetailsAction
    ): BenchmarkAsyncTask<KtorHttpResponse<List<Character>>, RumAutoLocationDetailsState.ResidentsLoadingTask> {
        return when (action) {
            is RumAutoLocationDetailsAction.ResidentsLoadingFinished -> {
                BenchmarkAsyncTask.Result(action.response, action.task)
            }

            else -> prev.residentsLoadingTask
        }
    }

    private fun characterIds(location: Location): List<String> {
        return location.residents.mapNotNull { it.split("/").lastOrNull() }
    }

    private fun launchResidentsLoadingTask(task: RumAutoLocationDetailsState.ResidentsLoadingTask): Job {
        return viewModelScope.launch(defaultDispatcher) {
            val response = rickAndMortyNetworkService.getCharacters(task.ids)
            dispatch(RumAutoLocationDetailsAction.ResidentsLoadingFinished(response, task))
        }
    }
}
