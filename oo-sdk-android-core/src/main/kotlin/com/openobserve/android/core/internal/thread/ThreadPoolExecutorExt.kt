/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.internal.thread

import com.openobserve.android.api.InternalLogger
import com.openobserve.android.internal.time.TimeProvider
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

internal const val MAX_SLEEP_DURATION_IN_MS = 10L

internal fun ThreadPoolExecutor.waitToIdle(
    timeoutInMs: Long,
    internalLogger: InternalLogger,
    timeProvider: TimeProvider
): Boolean {
    val startTime = timeProvider.getDeviceElapsedTimeNanos()
    val timeoutInNs = TimeUnit.MILLISECONDS.toNanos(timeoutInMs)
    val sleepDurationInMs = timeoutInMs.coerceIn(0, MAX_SLEEP_DURATION_IN_MS)
    var interrupted: Boolean
    do {
        if (isIdle()) {
            return true
        }
        interrupted = sleepSafe(sleepDurationInMs, internalLogger)
    } while (((timeProvider.getDeviceElapsedTimeNanos() - startTime) < timeoutInNs) && !interrupted)

    return isIdle()
}

internal fun ThreadPoolExecutor.isIdle(): Boolean {
    return (this.taskCount - this.completedTaskCount <= 0)
}
