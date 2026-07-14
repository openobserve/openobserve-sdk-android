/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark.sample.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
internal fun <T> ExpandedItemView(
    titleText: String,
    items: List<T>,
    headerText: String,
    itemTextFactory: (T) -> String,
    onClick: (T) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = titleText, modifier = Modifier.weight(1f))

        DropDownMenuView(
            headerText = headerText,
            items = items,
            textFactory = { Text(text = itemTextFactory(it)) },
            onClickAction = { onClick(it) }
        )
    }
}
