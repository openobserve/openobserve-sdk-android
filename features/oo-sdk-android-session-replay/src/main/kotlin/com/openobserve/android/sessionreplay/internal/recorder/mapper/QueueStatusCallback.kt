/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.internal.recorder.mapper

import com.openobserve.android.sessionreplay.internal.async.RecordedDataQueueRefs
import com.openobserve.android.sessionreplay.utils.AsyncJobStatusCallback

internal class QueueStatusCallback(
    private val recordedDataQueueRefs: RecordedDataQueueRefs
) : AsyncJobStatusCallback {

    override fun jobStarted() {
        recordedDataQueueRefs.incrementPendingJobs()
    }

    override fun jobFinished() {
        recordedDataQueueRefs.decrementPendingJobs()
        recordedDataQueueRefs.tryToConsumeItem()
    }
}
