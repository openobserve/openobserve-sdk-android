/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.internal.processor

import com.openobserve.android.sessionreplay.internal.async.ResourceRecordedDataQueueItem
import com.openobserve.android.sessionreplay.internal.async.SnapshotRecordedDataQueueItem
import com.openobserve.android.sessionreplay.internal.async.TouchEventRecordedDataQueueItem

internal interface Processor {

    fun processResources(item: ResourceRecordedDataQueueItem)

    fun processScreenSnapshots(item: SnapshotRecordedDataQueueItem)

    fun processTouchEventsRecords(item: TouchEventRecordedDataQueueItem)
}
