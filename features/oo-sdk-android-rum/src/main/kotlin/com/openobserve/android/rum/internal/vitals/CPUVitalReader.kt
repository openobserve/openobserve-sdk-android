/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.internal.vitals

import com.openobserve.android.api.InternalLogger
import com.openobserve.android.core.internal.persistence.file.canReadSafe
import com.openobserve.android.core.internal.persistence.file.existsSafe
import com.openobserve.android.core.internal.persistence.file.readTextSafe
import java.io.File

/**
 * Reads the CPU `utime` based on the `/proc/self/stat` file.
 * cf. documentation https://man7.org/linux/man-pages/man5/procfs.5.html
 */
internal class CPUVitalReader(
    internal val statFile: File = STAT_FILE,
    internal val internalLogger: InternalLogger
) : VitalReader {

    @Suppress("ReturnCount")
    override fun readVitalData(): Double? {
        if (!(statFile.existsSafe(internalLogger) && statFile.canReadSafe(internalLogger))) {
            return null
        }

        val stat = statFile.readTextSafe(internalLogger = internalLogger) ?: return null
        val tokens = stat.split(' ')
        return if (tokens.size > UTIME_IDX) {
            tokens[UTIME_IDX].toDoubleOrNull()
        } else {
            null
        }
    }

    companion object {

        private const val STAT_PATH = "/proc/self/stat"
        internal val STAT_FILE = File(STAT_PATH)

        private const val UTIME_IDX = 13
    }
}
