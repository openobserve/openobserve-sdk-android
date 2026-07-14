/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.internal.persistence.file.advanced

import androidx.annotation.WorkerThread
import com.openobserve.android.api.InternalLogger
import com.openobserve.android.core.internal.persistence.file.FileMover
import com.openobserve.android.core.internal.utils.retryWithDelay
import com.openobserve.android.internal.time.TimeProvider
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * A [DataMigrationOperation] that delete all the files in the `targetDir` directory.
 */
internal class WipeDataMigrationOperation(
    internal val targetDir: File?,
    internal val fileMover: FileMover,
    internal val internalLogger: InternalLogger,
    internal val timeProvider: TimeProvider
) : DataMigrationOperation {

    @WorkerThread
    override fun run() {
        if (targetDir == null) {
            internalLogger.log(
                InternalLogger.Level.WARN,
                InternalLogger.Target.MAINTAINER,
                { WARN_NULL_DIR }
            )
        } else {
            retryWithDelay(MAX_RETRY, RETRY_DELAY_NS, internalLogger, timeProvider) {
                fileMover.delete(targetDir)
            }
        }
    }

    companion object {
        internal const val WARN_NULL_DIR = "Can't wipe data from a null directory"

        private const val MAX_RETRY = 3
        private val RETRY_DELAY_NS = TimeUnit.MILLISECONDS.toNanos(500)
    }
}
