/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.internal.monitor

import android.app.Activity
import com.openobserve.android.core.feature.event.ThreadDump
import com.openobserve.android.internal.telemetry.InternalTelemetryEvent
import com.openobserve.android.rum.RumErrorSource
import com.openobserve.android.rum.RumMonitor
import com.openobserve.android.rum.RumPerformanceMetric
import com.openobserve.android.rum.internal.debug.RumDebugListener
import com.openobserve.android.rum.internal.startup.RumStartupScenario
import com.openobserve.android.rum.internal.startup.RumTTIDInfo
import com.openobserve.tools.annotation.NoOpImplementation

/**
 * FOR INTERNAL USAGE ONLY.
 */
@SuppressWarnings("ComplexInterface", "TooManyFunctions")
@NoOpImplementation
internal interface AdvancedRumMonitor : RumMonitor, AdvancedNetworkRumMonitor {

    fun resetSession()

    fun start()

    fun sendWebViewEvent()

    fun addLongTask(durationNs: Long, target: String)

    fun addSessionReplaySkippedFrame()

    fun addCrash(
        message: String,
        source: RumErrorSource,
        throwable: Throwable,
        threads: List<ThreadDump>
    )

    fun eventSent(viewId: String, event: StorageEvent)

    fun eventDropped(viewId: String, event: StorageEvent)

    fun setDebugListener(listener: RumDebugListener?)

    fun sendTelemetryEvent(telemetryEvent: InternalTelemetryEvent)

    fun updatePerformanceMetric(metric: RumPerformanceMetric, value: Double)

    fun updateExternalRefreshRate(frameTimeSeconds: Double)

    fun setInternalViewAttribute(key: String, value: Any?)

    fun setSyntheticsAttribute(testId: String, resultId: String)

    fun enableJankStatsTracking(activity: Activity)

    fun sendTTIDEvent(info: RumTTIDInfo)

    fun sendAppStartEvent(scenario: RumStartupScenario)
}
