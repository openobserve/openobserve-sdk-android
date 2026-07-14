/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.configuration

/**
 * @param capacity the maximum size of the queue
 * @param onThresholdReached callback called when the queue reaches full capacity
 * @param onItemDropped called when an item is dropped because of this backpressure strategy
 * @param backpressureMitigation the mitigation to use when reaching the capacity
 */
data class BackPressureStrategy(
    val capacity: Int,
    val onThresholdReached: () -> Unit,
    val onItemDropped: (Any) -> Unit,
    val backpressureMitigation: BackPressureMitigation
)
