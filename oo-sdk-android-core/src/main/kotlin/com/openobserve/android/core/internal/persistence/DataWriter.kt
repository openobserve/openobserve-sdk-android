/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.internal.persistence

import androidx.annotation.WorkerThread
import com.openobserve.tools.annotation.NoOpImplementation

/**
 * A class able to write a data (or list of data) with type [T] in a persistence location
 * (e.g. file, database, …).
 * @param T the type of data to store
 */
@NoOpImplementation
internal interface DataWriter<T : Any> {

    /**
     * Writes the element into the relevant location.
     */
    @WorkerThread
    fun write(element: T)

    /**
     * Writes a list of elements into the relevant location.
     */
    @WorkerThread
    fun write(data: List<T>)
}
