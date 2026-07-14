/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.forge

import com.openobserve.android.sessionreplay.internal.async.ResourceRecordedDataQueueItem
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory
import java.util.UUID

internal class ResourceRecordedDataQueueItemForgeryFactory : ForgeryFactory<ResourceRecordedDataQueueItem> {
    override fun getForgery(forge: Forge): ResourceRecordedDataQueueItem {
        return ResourceRecordedDataQueueItem(
            recordedQueuedItemContext = forge.getForgery(),
            identifier = forge.getForgery<UUID>().toString(),
            resourceData = forge.aString().toByteArray(),
            creationTimestampInNs = forge.aLong(min = 0)
        )
    }
}
