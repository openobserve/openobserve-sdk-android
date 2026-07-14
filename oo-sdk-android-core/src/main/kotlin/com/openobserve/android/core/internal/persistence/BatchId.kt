/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.internal.persistence

import java.io.File

internal data class BatchId(
    val id: String
) {

    fun matchesFile(file: File): Boolean {
        return file.extractFileId() == id
    }

    companion object {

        fun fromFile(file: File): BatchId {
            return BatchId(file.extractFileId())
        }

        private fun File.extractFileId(): String {
            return absolutePath
        }
    }
}
