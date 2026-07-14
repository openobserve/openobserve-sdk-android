/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.core.internal.time

import com.openobserve.android.api.context.TimeInfo
import com.openobserve.android.internal.time.TimeProvider
import java.util.concurrent.TimeUnit

internal fun TimeProvider.composeTimeInfo(): TimeInfo {
    val deviceTimeMs = getDeviceTimestampMillis()
    val serverTimeMs = getServerTimestampMillis()
    return TimeInfo(
        deviceTimeNs = TimeUnit.MILLISECONDS.toNanos(deviceTimeMs),
        serverTimeNs = TimeUnit.MILLISECONDS.toNanos(serverTimeMs),
        serverTimeOffsetNs = TimeUnit.MILLISECONDS.toNanos(serverTimeMs - deviceTimeMs),
        serverTimeOffsetMs = serverTimeMs - deviceTimeMs
    )
}
