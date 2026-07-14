/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.forge

import com.openobserve.android.sessionreplay.internal.async.SnapshotRecordedDataQueueItem
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class SnapshotRecordedDataQueueItemForgeryFactory : ForgeryFactory<SnapshotRecordedDataQueueItem> {
    override fun getForgery(forge: Forge): SnapshotRecordedDataQueueItem {
        val item = SnapshotRecordedDataQueueItem(
            recordedQueuedItemContext = forge.getForgery(),
            systemInformation = forge.getForgery(),
            creationTimestampInNs = forge.aLong(min = 0)
        )

        item.pendingJobs.set(forge.anInt())
        item.nodes = listOf(forge.getForgery())
        return item
    }
}
