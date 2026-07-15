/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.rum.internal.startup

import com.openobserve.android.core.InternalSdkCore
import com.openobserve.android.rum.OoRumContentProvider

internal interface RumAppStartupTelemetryReporter {
    fun reportTTID(info: RumTTIDInfo, indexInSession: Int)

    companion object {
        fun create(sdkCore: InternalSdkCore): RumAppStartupTelemetryReporter {
            return RumAppStartupTelemetryReporterImpl(
                internalLogger = sdkCore.internalLogger,
                appStartupTimeNs = sdkCore.appStartTimeNs,
                contentProviderCreationTimeNs = OoRumContentProvider.createTimeNs,
                processStartImportance = OoRumContentProvider.processImportance
            )
        }
    }
}
