/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.internal.metrics

import com.openobserve.tools.annotation.NoOpImplementation
import java.io.File

@NoOpImplementation
internal interface MetricsDispatcher {
    fun sendBatchDeletedMetric(batchFile: File, removalReason: RemovalReason, numPendingBatches: Int)

    fun sendBatchClosedMetric(batchFile: File, batchMetadata: BatchClosedMetadata)
}
