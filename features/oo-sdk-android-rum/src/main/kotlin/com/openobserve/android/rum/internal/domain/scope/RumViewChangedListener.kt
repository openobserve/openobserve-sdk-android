/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.internal.domain.scope

internal data class RumViewInfo(
    val key: RumScopeKey,
    val attributes: Map<String, Any?>,
    val isActive: Boolean
)

internal interface RumViewChangedListener {
    fun onViewChanged(viewInfo: RumViewInfo)
}
