/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.core.internal.data.upload

import com.openobserve.android.api.InternalLogger
import com.openobserve.android.core.configuration.UploadSchedulerStrategy
import com.openobserve.android.core.internal.ContextProvider
import com.openobserve.android.core.internal.net.info.NetworkInfoProvider
import com.openobserve.android.core.internal.persistence.Storage
import com.openobserve.android.core.internal.system.SystemInfoProvider
import com.openobserve.android.core.internal.utils.executeSafe
import java.util.concurrent.ScheduledThreadPoolExecutor

internal class DataUploadScheduler(
    private val featureName: String,
    storage: Storage,
    dataUploader: DataUploader,
    contextProvider: ContextProvider,
    networkInfoProvider: NetworkInfoProvider,
    systemInfoProvider: SystemInfoProvider,
    uploadSchedulerStrategy: UploadSchedulerStrategy,
    maxBatchesPerJob: Int,
    private val scheduledThreadPoolExecutor: ScheduledThreadPoolExecutor,
    private val internalLogger: InternalLogger
) : UploadScheduler {

    internal val runnable = DataUploadRunnable(
        featureName = featureName,
        threadPoolExecutor = scheduledThreadPoolExecutor,
        storage = storage,
        dataUploader = dataUploader,
        contextProvider = contextProvider,
        networkInfoProvider = networkInfoProvider,
        systemInfoProvider = systemInfoProvider,
        uploadSchedulerStrategy = uploadSchedulerStrategy,
        maxBatchesPerJob = maxBatchesPerJob,
        internalLogger = internalLogger
    )

    override fun startScheduling() {
        scheduledThreadPoolExecutor.executeSafe(
            "$featureName: data upload",
            internalLogger,
            runnable
        )
    }

    override fun stopScheduling() {
        scheduledThreadPoolExecutor.remove(runnable)
    }
}
