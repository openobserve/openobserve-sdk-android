/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.rum.internal.domain.state

internal data class SlowFrameRecord(
    val startTimestampNs: Long,
    var durationNs: Long
) {
    override fun toString(): String {
        return "${durationNs / NS_IN_MS}ms"
    }

    companion object {
        private const val NS_IN_MS = 1_000_000.0
    }
}
