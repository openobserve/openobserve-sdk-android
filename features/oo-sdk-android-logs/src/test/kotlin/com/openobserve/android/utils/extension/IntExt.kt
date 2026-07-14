/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.utils.extension

import android.util.Log
import com.openobserve.android.log.internal.domain.OpenObserveLogGenerator
import com.openobserve.android.log.model.LogEvent

fun Int.asLogStatus(): LogEvent.Status {
    return when (this) {
        Log.ASSERT -> LogEvent.Status.CRITICAL
        Log.ERROR -> LogEvent.Status.ERROR
        Log.WARN -> LogEvent.Status.WARN
        Log.INFO -> LogEvent.Status.INFO
        Log.DEBUG -> LogEvent.Status.DEBUG
        Log.VERBOSE -> LogEvent.Status.TRACE
        OpenObserveLogGenerator.CRASH -> LogEvent.Status.EMERGENCY
        else -> LogEvent.Status.DEBUG
    }
}
