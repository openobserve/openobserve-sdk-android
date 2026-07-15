/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */
package com.openobserve.android.trace.internal

import android.util.Log
import com.openobserve.android.api.InternalLogger
import com.openobserve.android.api.feature.Feature
import com.openobserve.android.api.feature.FeatureSdkCore
import com.openobserve.android.internal.utils.loggableStackTrace
import com.openobserve.android.log.LogAttributes
import com.openobserve.android.trace.api.OpenObserveTracingConstants
import com.openobserve.android.trace.api.span.OpenObserveSpan

internal class OpenObserveSpanLogger(
    private val sdkCore: FeatureSdkCore
) {

    fun log(message: String, span: OpenObserveSpan) {
        val fields = mutableMapOf<String, Any>(OpenObserveTracingConstants.LogAttributes.EVENT to message)
        extractError(fields, span)
        sendLogEvent(fields, span)
    }

    fun logErrorMessage(message: String, span: OpenObserveSpan) {
        val fields = mutableMapOf<String, Any>(
            OpenObserveTracingConstants.LogAttributes.MESSAGE to message,
            OpenObserveTracingConstants.LogAttributes.STATUS to Log.ERROR
        )
        extractError(fields, span)
        sendLogEvent(fields, span)
    }

    fun log(throwable: Throwable, span: OpenObserveSpan) {
        val fields = mutableMapOf<String, Any>(OpenObserveTracingConstants.LogAttributes.ERROR_OBJECT to throwable)
        extractError(fields, span)
        sendLogEvent(fields, span)
    }

    fun log(attributes: Map<String, Any>, span: OpenObserveSpan) {
        extractError(attributes.toMutableMap(), span)
        sendLogEvent(attributes.toMutableMap(), span)
    }

    private fun extractError(
        map: MutableMap<String, *>,
        span: OpenObserveSpan
    ) {
        val throwable = map.remove(OpenObserveTracingConstants.LogAttributes.ERROR_OBJECT) as? Throwable
        val kind = map.remove(LogAttributes.ERROR_KIND)
        val errorType = kind?.toString() ?: throwable?.javaClass?.name

        if (errorType != null) {
            val stackField = map.remove(OpenObserveTracingConstants.LogAttributes.STACK)
            val msgField = map[LogAttributes.MESSAGE]
            val stack = stackField?.toString() ?: throwable?.loggableStackTrace()
            val message = msgField?.toString() ?: throwable?.message

            span.isError = true
            span.setTag(OpenObserveTracingConstants.Tags.KEY_ERROR_TYPE, errorType)
            span.setTag(OpenObserveTracingConstants.Tags.KEY_ERROR_MSG, message)
            span.setTag(OpenObserveTracingConstants.Tags.KEY_ERROR_STACK, stack)
        }
    }

    private fun sendLogEvent(
        fields: MutableMap<String, Any>,
        span: OpenObserveSpan
    ) {
        val logsFeature = sdkCore.getFeature(Feature.LOGS_FEATURE_NAME)

        if (logsFeature != null && fields.isNotEmpty()) {
            val message = fields.remove(OpenObserveTracingConstants.LogAttributes.MESSAGE)
                ?.toString() ?: DEFAULT_EVENT_MESSAGE
            val logStatus = fields.remove(OpenObserveTracingConstants.LogAttributes.STATUS) ?: Log.VERBOSE
            fields[LogAttributes.DD_TRACE_ID] = span.context().traceId.toHexString()
            fields[LogAttributes.DD_SPAN_ID] = span.context().spanId.toString()
            val timestamp = sdkCore.timeProvider.getDeviceTimestampMillis()
            logsFeature.sendEvent(
                buildMap {
                    put("type", "span_log")
                    put("loggerName", TRACE_LOGGER_NAME)
                    put("message", message)
                    put("attributes", fields)
                    put("timestamp", timestamp)
                    put("logStatus", logStatus)
                }
            )
        } else if (logsFeature == null) {
            sdkCore.internalLogger.log(
                InternalLogger.Level.WARN,
                InternalLogger.Target.USER,
                { MISSING_LOG_FEATURE_INFO }
            )
        }
    }

    companion object Companion {
        internal const val TRACE_LOGGER_NAME = "trace"
        internal const val DEFAULT_EVENT_MESSAGE = "Span event"
        internal const val MISSING_LOG_FEATURE_INFO = "Requested to write span log, but Logs feature is not registered."
    }
}
