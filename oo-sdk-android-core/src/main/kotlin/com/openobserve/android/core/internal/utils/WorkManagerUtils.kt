/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.internal.utils

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.openobserve.android.api.InternalLogger
import com.openobserve.android.core.UploadWorker
import java.util.concurrent.TimeUnit

internal const val CANCEL_ERROR_MESSAGE = "Error cancelling the UploadWorker"
internal const val SETUP_ERROR_MESSAGE = "Error while trying to setup the UploadWorker"
internal const val UPLOAD_WORKER_WAS_SCHEDULED = "UploadWorker was scheduled."
internal const val UPLOAD_WORKER_NAME = "OpenObserveUploadWorker"
internal const val TAG_OPENOBSERVE_UPLOAD = "OpenObserveBackgroundUpload"

internal const val DELAY_MS: Long = 5000

internal fun cancelUploadWorker(
    context: Context,
    instanceName: String,
    internalLogger: InternalLogger
) {
    try {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelAllWorkByTag("$TAG_OPENOBSERVE_UPLOAD/$instanceName")
    } catch (e: IllegalStateException) {
        internalLogger.log(
            InternalLogger.Level.ERROR,
            listOf(InternalLogger.Target.MAINTAINER, InternalLogger.Target.TELEMETRY),
            { CANCEL_ERROR_MESSAGE },
            e
        )
    }
}

@Suppress("TooGenericExceptionCaught")
internal fun triggerUploadWorker(
    context: Context,
    instanceName: String,
    internalLogger: InternalLogger
) {
    try {
        val workManager = WorkManager.getInstance(context)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_ROAMING)
            .build()
        val uploadWorkRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
            .setConstraints(constraints)
            .addTag("$TAG_OPENOBSERVE_UPLOAD/$instanceName")
            .setInitialDelay(DELAY_MS, TimeUnit.MILLISECONDS)
            .setInputData(Data.Builder().putString(UploadWorker.OPENOBSERVE_INSTANCE_NAME, instanceName).build())
            .build()
        workManager.enqueueUniqueWork(
            UPLOAD_WORKER_NAME,
            ExistingWorkPolicy.REPLACE,
            uploadWorkRequest
        )
        internalLogger.log(
            InternalLogger.Level.INFO,
            InternalLogger.Target.MAINTAINER,
            { UPLOAD_WORKER_WAS_SCHEDULED }
        )
    } catch (e: Exception) {
        internalLogger.log(
            InternalLogger.Level.ERROR,
            listOf(InternalLogger.Target.MAINTAINER, InternalLogger.Target.TELEMETRY),
            { SETUP_ERROR_MESSAGE },
            e
        )
    }
}
