/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.internal.utils

import com.openobserve.android.api.SdkCore
import com.openobserve.android.rum.GlobalRumMonitor
import com.openobserve.android.rum.RumErrorSource

internal const val CLOSABLE_ERROR_MESSAGE = "Error while using the closeable"

internal fun handleClosableError(throwable: Throwable, sdkCore: SdkCore) {
    GlobalRumMonitor.get(sdkCore).addError(CLOSABLE_ERROR_MESSAGE, RumErrorSource.SOURCE, throwable)
}
