/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.forge

import com.openobserve.android.sessionreplay.internal.async.TouchEventRecordedDataQueueItem
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class TouchEventRecordedDataQueueItemForgeryFactory : ForgeryFactory<TouchEventRecordedDataQueueItem> {
    override fun getForgery(forge: Forge): TouchEventRecordedDataQueueItem {
        return TouchEventRecordedDataQueueItem(
            recordedQueuedItemContext = forge.getForgery(),
            creationTimestampInNs = forge.aLong(min = 0),
            touchData = listOf(forge.getForgery())
        )
    }
}
