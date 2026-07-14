/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.internal

import android.app.Application
import com.openobserve.android.sessionreplay.internal.recorder.Recorder
import com.openobserve.android.sessionreplay.internal.resources.ResourceDataStoreManager
import com.openobserve.android.sessionreplay.internal.storage.RecordWriter
import com.openobserve.android.sessionreplay.internal.storage.ResourcesWriter
import com.openobserve.android.sessionreplay.internal.utils.RumContextProvider

internal fun interface RecorderProvider {
    fun provideSessionReplayRecorder(
        resourceDataStoreManager: ResourceDataStoreManager,
        resourceWriter: ResourcesWriter,
        recordWriter: RecordWriter,
        rumContextProvider: RumContextProvider,
        application: Application
    ): Recorder
}
