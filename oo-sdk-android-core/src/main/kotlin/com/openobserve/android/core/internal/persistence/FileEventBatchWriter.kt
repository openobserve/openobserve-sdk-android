/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.internal.persistence

import androidx.annotation.WorkerThread
import com.openobserve.android.api.InternalLogger
import com.openobserve.android.api.storage.EventBatchWriter
import com.openobserve.android.api.storage.EventType
import com.openobserve.android.api.storage.RawBatchEvent
import com.openobserve.android.core.internal.persistence.file.FileOrchestrator
import com.openobserve.android.core.internal.persistence.file.FilePersistenceConfig
import com.openobserve.android.core.internal.persistence.file.FileReaderWriter
import com.openobserve.android.core.internal.persistence.file.FileWriter
import com.openobserve.android.core.internal.persistence.file.existsSafe
import java.io.File
import java.util.Locale

internal class FileEventBatchWriter(
    private val fileOrchestrator: FileOrchestrator,
    private val eventsWriter: FileWriter<RawBatchEvent>,
    private val metadataReaderWriter: FileReaderWriter,
    private val filePersistenceConfig: FilePersistenceConfig,
    private val batchWriteEventListener: BatchWriteEventListener,
    private val internalLogger: InternalLogger
) : EventBatchWriter {

    @get:WorkerThread
    private val batchFile: File? by lazy {
        @Suppress("ThreadSafety") // called in the worker context
        fileOrchestrator.getWritableFile()
    }

    @get:WorkerThread
    private val metadataFile: File?
        get() = batchFile?.let {
            @Suppress("ThreadSafety") // called in the worker context
            fileOrchestrator.getMetadataFile(it)
        }

    @WorkerThread
    override fun currentMetadata(): ByteArray? {
        return with(metadataFile) {
            if (this == null || !existsSafe(internalLogger)) {
                null
            } else {
                metadataReaderWriter.readData(this)
            }
        }
    }

    @WorkerThread
    override fun write(
        event: RawBatchEvent,
        batchMetadata: ByteArray?,
        eventType: EventType
    ): Boolean {
        val (batchFile, metadataFile) = batchFile to metadataFile
        if (batchFile == null) {
            internalLogger.log(
                InternalLogger.Level.ERROR,
                targets = listOf(InternalLogger.Target.USER, InternalLogger.Target.TELEMETRY),
                { NO_BATCH_FILE_AVAILABLE }
            )
            return false
        }

        // prevent useless operation for empty event
        return if (event.data.isEmpty()) {
            true
        } else if (!checkEventSize(event.data.size)) {
            false
        } else if (eventsWriter.writeData(batchFile, event, true)) {
            batchWriteEventListener.onWriteEvent(event.data.size.toLong())
            if (batchMetadata?.isNotEmpty() == true && metadataFile != null) {
                writeBatchMetadata(metadataFile, batchMetadata)
            }
            true
        } else {
            false
        }
    }

    private fun checkEventSize(eventSize: Int): Boolean {
        if (eventSize > filePersistenceConfig.maxItemSize) {
            internalLogger.log(
                InternalLogger.Level.ERROR,
                InternalLogger.Target.USER,
                {
                    ERROR_LARGE_DATA.format(
                        Locale.US,
                        eventSize,
                        filePersistenceConfig.maxItemSize
                    )
                }
            )
            return false
        }
        return true
    }

    @WorkerThread
    private fun writeBatchMetadata(metadataFile: File, metadata: ByteArray) {
        val result = metadataReaderWriter.writeData(
            metadataFile,
            metadata,
            false
        )
        if (!result) {
            internalLogger.log(
                InternalLogger.Level.WARN,
                InternalLogger.Target.USER,
                {
                    WARNING_METADATA_WRITE_FAILED.format(
                        Locale.US,
                        metadataFile.path
                    )
                }
            )
        }
    }

    companion object {
        internal const val WARNING_METADATA_WRITE_FAILED = "Unable to write metadata file: %s"
        internal const val ERROR_LARGE_DATA = "Can't write data with size %d (max item size is %d)"
        internal const val NO_BATCH_FILE_AVAILABLE = "No batch file available"
    }
}
