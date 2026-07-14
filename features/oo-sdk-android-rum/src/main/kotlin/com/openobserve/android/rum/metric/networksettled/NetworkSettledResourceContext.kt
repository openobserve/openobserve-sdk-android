/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.metric.networksettled

/**
 * Represents the context of a network resource that has settled.
 *
 * @property resourceId The unique identifier of the network resource.
 * @property eventCreatedAtNanos The timestamp (in nanoseconds) when the event was created.
 * @property viewCreatedTimestamp The timestamp (in nanoseconds) when the view was created, or null if not applicable.
 */
data class NetworkSettledResourceContext(
    val resourceId: String,
    val eventCreatedAtNanos: Long,
    val viewCreatedTimestamp: Long?
)
