/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.internal.persistence.file

import androidx.annotation.WorkerThread
import java.io.File

internal interface FileReader<T> {

    /**
     * Reads data from the given file.
     *  @param file the file to read from
     *  @return the data
     */
    @WorkerThread
    fun readData(file: File): T
}
