/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.internal.async

import com.openobserve.android.sessionreplay.model.MobileSegment
import com.openobserve.android.sessionreplay.recorder.SystemInformation

internal interface DataQueueHandler {
    fun addResourceItem(
        identifier: String,
        resourceData: ByteArray,
        mimeType: String? = null
    ): ResourceRecordedDataQueueItem?
    fun addTouchEventItem(
        pointerInteractions: List<MobileSegment.MobileRecord>
    ): TouchEventRecordedDataQueueItem?
    fun addSnapshotItem(systemInformation: SystemInformation): SnapshotRecordedDataQueueItem?
    fun tryToConsumeItems()
    fun clearAndStopProcessingQueue()
}
