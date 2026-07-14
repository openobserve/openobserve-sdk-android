/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.internal.metric

import com.openobserve.android.api.InternalLogger
import com.openobserve.android.core.metrics.PerformanceMetric
import com.openobserve.android.core.metrics.TelemetryMetricType
import com.openobserve.android.internal.telemetry.InternalTelemetryEvent

class FakeInternalLogger : InternalLogger {

    var lastMetric: Pair<String, Map<String, Any?>>? = null

    var errorLog: String? = null

    override fun log(
        level: InternalLogger.Level,
        target: InternalLogger.Target,
        messageBuilder: () -> String,
        throwable: Throwable?,
        onlyOnce: Boolean,
        additionalProperties: Map<String, Any?>?
    ) {
        errorLog = messageBuilder()
    }

    override fun log(
        level: InternalLogger.Level,
        targets: List<InternalLogger.Target>,
        messageBuilder: () -> String,
        throwable: Throwable?,
        onlyOnce: Boolean,
        additionalProperties: Map<String, Any?>?
    ) {
        // do nothing
    }

    override fun logMetric(
        messageBuilder: () -> String,
        additionalProperties: Map<String, Any?>,
        samplingRate: Float,
        creationSampleRate: Float?
    ) {
        lastMetric = Pair(messageBuilder(), additionalProperties)
    }

    override fun startPerformanceMeasure(
        callerClass: String,
        metric: TelemetryMetricType,
        samplingRate: Float,
        operationName: String
    ): PerformanceMetric? {
        // do nothing
        return null
    }

    override fun logApiUsage(
        samplingRate: Float,
        apiUsageEventBuilder: () -> InternalTelemetryEvent.ApiUsage
    ) {
        // do nothing
    }
}
