/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark.sample.utils

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flowOn
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@Composable
internal fun <T, I> LastItemTracker(
    allItems: List<T>,
    itemId: (T) -> I,
    lazyListState: LazyListState,
    onEndReached: () -> Unit
) {
    LaunchedEffect(allItems) {
        val lastItemChanges = snapshotFlow {
            val lastItem = lazyListState
                .layoutInfo
                .visibleItemsInfo
                .lastOrNull()
                ?.key

            val indexOfLastVisible = allItems.indexOfFirst { itemId(it) == lastItem }

            indexOfLastVisible to allItems.lastIndex
        }

        lastItemChanges
            .distinctUntilChangedBy { it.first }
            .debounce(500.milliseconds)
            .flowOn(Dispatchers.Default)
            .collect { (indexOfLastVisible, lastIndex) ->
                if (indexOfLastVisible == lastIndex) {
                    onEndReached()
                }
            }
    }
}
