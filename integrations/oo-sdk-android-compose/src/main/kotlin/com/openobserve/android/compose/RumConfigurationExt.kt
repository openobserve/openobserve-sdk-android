/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.compose

import com.openobserve.android.compose.internal.ComposeActionTrackingStrategy
import com.openobserve.android.rum.RumConfiguration
import com.openobserve.android.rum._RumInternalProxy

/**
 * Enable Jetpack Compose automatic actions tracking, such as
 * tap and scroll. Jetpack Compose actions tracking will be disabled if this API is not called,
 * which is the default behavior.
 *
 */
fun RumConfiguration.Builder.enableComposeActionTracking(): RumConfiguration.Builder {
    _RumInternalProxy.setComposeActionTrackingStrategy(
        builder = this,
        composeActionTrackingStrategy = ComposeActionTrackingStrategy()
    )
    return this
}
