/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.internal.recorder

import android.os.Handler
import android.os.Looper
import com.openobserve.android.api.feature.Feature
import com.openobserve.android.api.feature.FeatureSdkCore
import java.util.concurrent.TimeUnit

internal class Debouncer(
    private val handler: Handler = Handler(Looper.getMainLooper()),
    private val maxRecordDelayInNs: Long = MAX_DELAY_THRESHOLD_NS,
    private val timeBank: TimeBank = RecordingTimeBank(),
    private val sdkCore: FeatureSdkCore,
    private val dynamicOptimizationEnabled: Boolean
) {

    private var lastTimeRecordWasPerformed = 0L
    private var firstRequest = true

    internal fun debounce(runnable: Runnable) {
        if (firstRequest) {
            // we will initialize the lastTimeRecordWasPerformed here to the current time in nano
            // reason why we are not initializing this in the constructor is that in case the
            // component was initialized earlier than the first debounce request was requested
            // it will execute the runnable directly and will not pass through the handler.
            lastTimeRecordWasPerformed = sdkCore.timeProvider.getDeviceElapsedTimeNanos()
            firstRequest = false
        }
        handler.removeCallbacksAndMessages(null)
        val timePassedSinceLastExecution = sdkCore.timeProvider.getDeviceElapsedTimeNanos() - lastTimeRecordWasPerformed
        if (timePassedSinceLastExecution >= maxRecordDelayInNs) {
            executeRunnable(runnable)
        } else {
            handler.postDelayed({ executeRunnable(runnable) }, DEBOUNCE_TIME_IN_MS)
        }
    }

    private fun executeRunnable(runnable: Runnable) {
        if (dynamicOptimizationEnabled) {
            runInTimeBalance {
                runnable.run()
            }
        } else {
            runnable.run()
        }
        lastTimeRecordWasPerformed = sdkCore.timeProvider.getDeviceElapsedTimeNanos()
    }

    private fun runInTimeBalance(block: () -> Unit) {
        if (timeBank.updateAndCheck(sdkCore.timeProvider.getDeviceElapsedTimeNanos())) {
            val startTimeInNano = sdkCore.timeProvider.getDeviceElapsedTimeNanos()
            block()
            val endTimeInNano = sdkCore.timeProvider.getDeviceElapsedTimeNanos()
            timeBank.consume(endTimeInNano - startTimeInNano)
        } else {
            logSkippedFrame()
        }
    }

    private fun logSkippedFrame() {
        val rumFeature = sdkCore.getFeature(Feature.RUM_FEATURE_NAME) ?: return
        val telemetryEvent = mapOf(TYPE_KEY to TYPE_VALUE)
        rumFeature.sendEvent(telemetryEvent)
    }

    companion object {
        // one frame time
        private val MAX_DELAY_THRESHOLD_NS: Long = TimeUnit.MILLISECONDS.toNanos(64)

        // one frame time
        internal const val DEBOUNCE_TIME_IN_MS: Long = 64

        private const val TYPE_VALUE = "sr_skipped_frame"
        private const val TYPE_KEY = "type"
    }
}
