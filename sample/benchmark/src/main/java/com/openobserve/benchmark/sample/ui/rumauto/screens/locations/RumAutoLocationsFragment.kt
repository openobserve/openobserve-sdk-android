/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark.sample.ui.rumauto.screens.locations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.openobserve.benchmark.sample.activities.scenarios.benchmarkActivityComponent
import com.openobserve.benchmark.sample.ui.rumauto.screens.locations.di.DaggerRumAutoLocationsComponent
import com.openobserve.benchmark.sample.ui.rumauto.screens.locations.di.RumAutoLocationsComponent
import com.openobserve.benchmark.sample.utils.componentHolderViewModel
import com.openobserve.benchmark.sample.utils.recycler.applyNewItems
import com.openobserve.benchmark.sample.utils.recycler.trackRecyclerViewVisibleItems
import com.openobserve.sample.benchmark.databinding.FragmentRumAutoLocationsBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class RumAutoLocationsFragment : Fragment() {

    private val component: RumAutoLocationsComponent by componentHolderViewModel {
        DaggerRumAutoLocationsComponent.factory().create(
            deps = requireActivity().benchmarkActivityComponent,
            viewModelScope = viewModelScope
        )
    }

    @Inject
    lateinit var adapter: RumAutoLocationsAdapter

    @Inject
    lateinit var viewModel: RumAutoLocationsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        component.inject(this)

        val binding = FragmentRumAutoLocationsBinding.inflate(inflater, container, false)

        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.locationsRecycler.layoutManager = layoutManager
        binding.locationsRecycler.adapter = adapter

        viewModel.state
            .onEach { items ->
                adapter.applyNewItems(items)
            }
            .launchIn(lifecycleScope)

        trackRecyclerViewVisibleItems(
            recyclerView = binding.locationsRecycler,
            layoutManager = layoutManager,
            adapter = adapter
        ).onEach { items ->
            viewModel.dispatch(RumAutoLocationsAction.VisibleItemsChanged(items))
        }.launchIn(lifecycleScope)

        return binding.root
    }
}
