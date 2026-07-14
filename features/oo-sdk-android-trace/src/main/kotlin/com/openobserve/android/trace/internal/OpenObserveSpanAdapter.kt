/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.trace.internal

import com.openobserve.android.trace.api.span.OpenObserveSpan
import com.openobserve.android.trace.api.trace.OpenObserveTraceId
import com.openobserve.trace.bootstrap.instrumentation.api.AgentSpan
import com.openobserve.trace.core.DDSpan

internal class OpenObserveSpanAdapter(
    internal val delegate: AgentSpan,
    private val spanLogger: OpenObserveSpanLogger
) : OpenObserveSpan {

    override val isRootSpan: Boolean get() = delegate is DDSpan && delegate.isRootSpan

    override val traceId: OpenObserveTraceId get() = OpenObserveTraceIdAdapter(delegate.traceId)

    override val parentSpanId: Long? get() = (delegate as? DDSpan)?.parentId

    override val samplingPriority: Int? get() = delegate.traceSamplingPriority

    override val durationNano: Long get() = delegate.durationNano

    override val startTimeNanos: Long get() = delegate.startTime

    override val localRootSpan: OpenObserveSpan? get() = delegate.localRootSpan?.let { OpenObserveSpanAdapter(it, spanLogger) }

    override var isError: Boolean?
        get() = delegate.isError
        set(value) {
            if (value == null) return
            delegate.isError = value
        }

    override var resourceName: String?
        get() = delegate.resourceName?.toString()
        set(value) {
            delegate.resourceName = value
        }

    override var serviceName: String
        get() = delegate.serviceName
        set(value) {
            delegate.serviceName = value
        }

    override var operationName: String
        get() = delegate.operationName.toString()
        set(value) {
            delegate.operationName = value
        }

    override fun drop() = delegate.drop()

    override fun finish() = delegate.finish()

    override fun finish(finishMicros: Long) = delegate.finish(finishMicros)

    override fun context() = OpenObserveSpanContextAdapter(delegate.context())

    override fun setTag(tag: String?, value: String?) {
        delegate.setTag(tag, value)
    }

    override fun setTag(tag: String?, value: Boolean) {
        delegate.setTag(tag, value)
    }

    override fun setTag(tag: String?, value: Number?) {
        delegate.setTag(tag, value)
    }

    override fun setTag(tag: String?, value: Any?) {
        delegate.setTag(tag, value)
    }

    override fun getTag(tag: String?): Any? {
        return delegate.getTag(tag)
    }

    override fun setMetric(key: String, value: Int) {
        delegate.setMetric(key, value)
    }

    override fun setErrorMessage(message: String?) {
        delegate.setErrorMessage(message)
    }

    override fun addThrowable(throwable: Throwable) {
        delegate.addThrowable(throwable)
    }

    override fun logThrowable(throwable: Throwable) {
        spanLogger.log(throwable, this)
    }

    override fun logErrorMessage(message: String) {
        spanLogger.logErrorMessage(message, this)
    }

    override fun logMessage(message: String) {
        spanLogger.log(message, this)
    }

    override fun logAttributes(attributes: Map<String, Any>) {
        spanLogger.log(attributes, this)
    }

    internal fun addThrowable(throwable: Throwable, errorPriority: Byte) {
        delegate.addThrowable(throwable, errorPriority)
    }
}
