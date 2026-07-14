/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.api.storage

import androidx.annotation.WorkerThread
import com.openobserve.tools.annotation.NoOpImplementation

/**
 * Interface to be implemented by the class which wants to write arbitrary data with the
 * given [EventBatchWriter].
 */
@NoOpImplementation(publicNoOpImplementation = true)
interface DataWriter<T> {
    /**
     * Writes the element with a given [EventBatchWriter].
     *
     * @param writer the writer to use
     * @param element the event to write
     * @param eventType additional info about the event
     *
     * @return true if element was written, false otherwise.
     */
    @WorkerThread
    fun write(writer: EventBatchWriter, element: T, eventType: EventType): Boolean
}
