/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.utils

import com.openobserve.tools.annotation.NoOpImplementation

/**
 * A callback to be notified when an async job starts or finishes.
 */
@NoOpImplementation
interface AsyncJobStatusCallback {

    /**
     * Notifies that an async job has started.
     */
    fun jobStarted()

    /**
     * Notifies that an async job has finished.
     */
    fun jobFinished()
}
