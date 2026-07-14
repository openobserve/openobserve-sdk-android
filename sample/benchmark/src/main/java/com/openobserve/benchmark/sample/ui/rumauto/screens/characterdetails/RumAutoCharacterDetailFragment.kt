/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark.sample.ui.rumauto.screens.characterdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openobserve.benchmark.sample.activities.scenarios.benchmarkActivityComponent
import com.openobserve.benchmark.sample.navigation.args
import com.openobserve.benchmark.sample.network.rickandmorty.models.Character
import javax.inject.Inject

internal class RumAutoCharacterDetailFragment : Fragment() {

    private val character: Character by args()

    @Inject
    internal lateinit var viewModelFactory: AssistedRumAutoCharacterDetailViewModelFactory

    private val viewModel: RumAutoCharacterDetailsViewModel by viewModels {
        viewModelFactory.create(character)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().benchmarkActivityComponent.inject(this)

        return ComposeView(requireContext()).apply {
            setContent {
                val state by viewModel.state.collectAsStateWithLifecycle()
                RumAutoCharacterScreen(state, dispatch = viewModel::dispatch)
            }
        }
    }
}
