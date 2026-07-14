/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.internal.persistence

import androidx.annotation.WorkerThread
import com.openobserve.tools.annotation.NoOpImplementation

/**
 * A class able to read data from a persistence location (e.g. file, database, …).
 */
@NoOpImplementation
internal interface DataReader {

    /**
     * Reads the next piece of data and lock it so that it can't be read or written to by anyone.
     */
    @WorkerThread
    fun lockAndReadNext(): Batch?

    /**
     * Marks the data as read and releases it to be read/written to by someone else.
     */
    @WorkerThread
    fun release(data: Batch)

    /**
     * Marks the data as read and deletes it.
     */
    @WorkerThread
    fun drop(data: Batch)

    /**
     * Drop all available data.
     */
    @WorkerThread
    fun dropAll()
}
