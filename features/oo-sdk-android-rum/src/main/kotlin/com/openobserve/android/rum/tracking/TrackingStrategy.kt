/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.tracking

import android.content.Context
import com.openobserve.android.api.SdkCore
import com.openobserve.tools.annotation.NoOpImplementation

/**
 * The TrackingStrategy interface.
 */
@NoOpImplementation
interface TrackingStrategy {

    /**
     * This method will register the tracking strategy to the current Context and SDK instance.
     * @param sdkCore as [SdkCore]
     * @param context as [Context]
     */
    fun register(sdkCore: SdkCore, context: Context)

    /**
     * This method will unregister the tracking strategy from the current Context.
     * @param context as [Context]
     */
    fun unregister(context: Context?)
}
