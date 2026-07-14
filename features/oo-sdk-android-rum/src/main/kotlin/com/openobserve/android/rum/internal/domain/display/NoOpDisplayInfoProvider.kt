/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.internal.domain.display

import com.openobserve.android.rum.internal.domain.InfoProvider

internal class NoOpDisplayInfoProvider : InfoProvider<DisplayInfo> {
    override fun getState(): DisplayInfo {
        return DisplayInfo()
    }

    override fun cleanup() {}
}
