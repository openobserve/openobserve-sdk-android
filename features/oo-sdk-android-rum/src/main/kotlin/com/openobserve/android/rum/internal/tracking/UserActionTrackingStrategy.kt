/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.rum.internal.tracking

import com.openobserve.android.rum.internal.instrumentation.gestures.GesturesTracker
import com.openobserve.android.rum.tracking.TrackingStrategy
import com.openobserve.tools.annotation.NoOpImplementation

/**
 * A TrackingStrategy dedicated to user actions tracking.
 */
@NoOpImplementation
internal interface UserActionTrackingStrategy : TrackingStrategy {
    fun getGesturesTracker(): GesturesTracker
}
