/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.insights.internal.domain

internal sealed class TimelineEvent(
    val durationNs: Long
) {
    object Action : TimelineEvent(0L)
    object Tick : TimelineEvent(0L)
    class SlowFrame(durationNs: Long) : TimelineEvent(durationNs)
    class Resource(durationNs: Long) : TimelineEvent(durationNs)
    class LongTask(durationNs: Long) : TimelineEvent(durationNs)
}
