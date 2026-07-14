/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.compose.internal

import androidx.compose.runtime.State
import com.openobserve.android.rum.RumActionType
import com.openobserve.android.rum.RumAttributes
import com.openobserve.android.rum.RumMonitor

internal class TapActionTracker(
    private val targetName: String,
    private val attributes: Map<String, Any?> = emptyMap(),
    private val onTap: State<() -> Unit>,
    private val rumMonitor: RumMonitor
) : () -> Unit {
    override fun invoke() {
        rumMonitor.addAction(
            RumActionType.TAP,
            targetName,
            attributes + mapOf(
                RumAttributes.ACTION_TARGET_TITLE to targetName
            )
        )
        // that is user code, not ours
        @Suppress("UnsafeThirdPartyFunctionCall")
        onTap.value.invoke()
    }
}
