/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.internal.persistence.file.advanced

import androidx.annotation.WorkerThread
import com.openobserve.android.core.internal.persistence.file.FileOrchestrator

internal interface DataMigrator<S : Any> {

    @WorkerThread
    fun migrateData(
        previousState: S?,
        previousFileOrchestrator: FileOrchestrator,
        newState: S,
        newFileOrchestrator: FileOrchestrator
    )
}
