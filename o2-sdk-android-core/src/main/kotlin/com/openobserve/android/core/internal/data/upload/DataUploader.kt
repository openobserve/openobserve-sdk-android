/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.core.internal.data.upload

import com.openobserve.android.api.context.OpenObserveContext
import com.openobserve.android.api.storage.RawBatchEvent
import com.openobserve.android.core.internal.persistence.BatchId

internal interface DataUploader {
    fun upload(
        context: OpenObserveContext,
        batch: List<RawBatchEvent>,
        batchMeta: ByteArray?,
        batchId: BatchId? = null
    ): UploadStatus
}
