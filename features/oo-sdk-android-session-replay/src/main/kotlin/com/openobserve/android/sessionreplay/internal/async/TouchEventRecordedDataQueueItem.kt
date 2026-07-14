/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.internal.async

import com.openobserve.android.sessionreplay.internal.processor.RecordedQueuedItemContext
import com.openobserve.android.sessionreplay.model.MobileSegment

internal class TouchEventRecordedDataQueueItem(
    recordedQueuedItemContext: RecordedQueuedItemContext,
    creationTimestampInNs: Long,
    internal val touchData: List<MobileSegment.MobileRecord> = emptyList()
) : RecordedDataQueueItem(recordedQueuedItemContext, creationTimestampInNs) {

    override fun isValid(): Boolean {
        return touchData.isNotEmpty()
    }

    override fun isReady(): Boolean {
        return true
    }
}
