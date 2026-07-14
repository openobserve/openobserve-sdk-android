/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark.sample.ui.rumauto.screens.episodedetails

import com.openobserve.benchmark.sample.ui.rumauto.screens.common.details.characterItemDelegate
import com.openobserve.benchmark.sample.utils.recycler.BaseRecyclerViewItem
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import javax.inject.Inject

internal class RumAutoEpisodeDetailsAdapter @Inject constructor(
    private val viewModel: RumAutoEpisodeDetailsViewModel
) : ListDelegationAdapter<List<BaseRecyclerViewItem>>() {
    init {
        delegatesManager.apply {
            addDelegate(
                characterItemDelegate { viewModel.dispatch(RumAutoEpisodeDetailsAction.OnCharacterClicked(it)) }
            )
        }
    }
}
