/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.internal.persistence.file.batch

import com.openobserve.android.api.InternalLogger
import com.openobserve.android.api.storage.RawBatchEvent
import com.openobserve.android.core.internal.persistence.file.FileWriter
import com.openobserve.android.security.Encryption

internal interface BatchFileReaderWriter : FileWriter<RawBatchEvent>, BatchFileReader {

    companion object {
        /**
         * Creates either plain [PlainBatchFileReaderWriter] or [PlainBatchFileReaderWriter] wrapped in
         * [EncryptedBatchReaderWriter] if encryption is provided.
         */
        fun create(internalLogger: InternalLogger, encryption: Encryption?): BatchFileReaderWriter {
            val readerWriter = PlainBatchFileReaderWriter(internalLogger)
            return if (encryption == null) {
                readerWriter
            } else {
                EncryptedBatchReaderWriter(
                    encryption,
                    readerWriter,
                    internalLogger
                )
            }
        }
    }
}
