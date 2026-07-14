/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.sample.automotive.screen

import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.OnClickListener
import com.openobserve.android.OpenObserve
import com.openobserve.android.api.SdkCore
import com.openobserve.android.rum.GlobalRumMonitor
import com.openobserve.android.rum.RumActionType

internal fun Screen.monitorGetTemplate(
    sdkCore: SdkCore = OpenObserve.getInstance()
) {
    GlobalRumMonitor.get(sdkCore).startView(
        key = javaClass.name,
        name = javaClass.simpleName
    )
}

internal fun Action.Builder.setMonitoredClickListener(
    sdkCore: SdkCore = OpenObserve.getInstance(),
    listener: OnClickListener
): Action.Builder {
    val builtAction = build()
    return setOnClickListener {
        GlobalRumMonitor.get(sdkCore).addAction(
            type = RumActionType.TAP,
            name = builtAction.title?.toString() ?: builtAction.icon.toString()
        )
        listener.onClick()
    }
}
