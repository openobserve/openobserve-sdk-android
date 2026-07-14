/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.internal.processor

import androidx.annotation.MainThread
import com.openobserve.android.sessionreplay.SessionReplayInternalResourceQueue
import com.openobserve.android.sessionreplay.internal.async.RecordedDataQueueHandler

internal class ResourceQueueImpl(
    private val internalHandler: RecordedDataQueueHandler
) : SessionReplayInternalResourceQueue {
    @MainThread
    override fun addResourceItem(identifier: String, resourceData: ByteArray, mimeType: String?) {
        internalHandler.addResourceItem(identifier, resourceData, mimeType)
    }
}
