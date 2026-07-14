/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.internal.instrumentation.gestures

import android.content.Context
import android.view.Window
import com.openobserve.android.api.SdkCore
import com.openobserve.tools.annotation.NoOpImplementation

@NoOpImplementation
internal interface GesturesTracker {

    fun startTracking(
        window: Window?,
        context: Context,
        sdkCore: SdkCore
    )

    fun stopTracking(window: Window?, context: Context)
}
