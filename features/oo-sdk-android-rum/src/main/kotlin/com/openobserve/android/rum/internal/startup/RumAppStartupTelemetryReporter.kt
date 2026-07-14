/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.internal.startup

import com.openobserve.android.core.InternalSdkCore
import com.openobserve.android.rum.DdRumContentProvider

internal interface RumAppStartupTelemetryReporter {
    fun reportTTID(info: RumTTIDInfo, indexInSession: Int)

    companion object {
        fun create(sdkCore: InternalSdkCore): RumAppStartupTelemetryReporter {
            return RumAppStartupTelemetryReporterImpl(
                internalLogger = sdkCore.internalLogger,
                appStartupTimeNs = sdkCore.appStartTimeNs,
                contentProviderCreationTimeNs = DdRumContentProvider.createTimeNs,
                processStartImportance = DdRumContentProvider.processImportance
            )
        }
    }
}
