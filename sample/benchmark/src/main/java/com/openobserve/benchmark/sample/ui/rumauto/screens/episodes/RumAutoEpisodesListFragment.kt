/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark.sample.ui.rumauto.screens.episodes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openobserve.benchmark.sample.activities.scenarios.benchmarkActivityComponent
import javax.inject.Inject

internal class RumAutoEpisodesListFragment : Fragment() {
    @Inject
    internal lateinit var viewModelFactory: RumAutoEpisodesListViewModelFactory

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requireActivity().benchmarkActivityComponent.inject(this)

        val viewModel by viewModels<RumAutoEpisodesListViewModel> {
            viewModelFactory
        }

        return ComposeView(requireContext()).apply {
            setContent {
                val state by viewModel.state.collectAsStateWithLifecycle()

                RumAutoEpisodesListScreen(
                    modifier = Modifier.fillMaxSize(),
                    state = state,
                    dispatch = viewModel::dispatch
                )
            }
        }
    }
}
