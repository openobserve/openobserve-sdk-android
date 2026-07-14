/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.trace.internal.data

import androidx.annotation.WorkerThread
import com.openobserve.android.api.InternalLogger
import com.openobserve.android.api.context.OpenObserveContext
import com.openobserve.android.api.feature.Feature
import com.openobserve.android.api.feature.FeatureSdkCore
import com.openobserve.android.api.storage.EventBatchWriter
import com.openobserve.android.api.storage.EventType
import com.openobserve.android.api.storage.RawBatchEvent
import com.openobserve.android.event.EventMapper
import com.openobserve.android.event.NoOpEventMapper
import com.openobserve.android.trace.internal.RumContextPropagator
import com.openobserve.android.trace.internal.RumContextPropagator.Companion.extractRumContext
import com.openobserve.android.trace.internal.domain.event.ContextAwareMapper
import com.openobserve.android.trace.internal.storage.ContextAwareSerializer
import com.openobserve.android.trace.model.SpanEvent
import com.openobserve.trace.api.sampling.PrioritySampling
import com.openobserve.trace.common.writer.Writer
import com.openobserve.trace.core.DDSpan
import java.util.Locale

internal class CoreTraceWriter(
    private val sdkCore: FeatureSdkCore,
    internal val ddSpanToSpanEventMapper: ContextAwareMapper<DDSpan, SpanEvent>,
    internal val eventMapper: EventMapper<SpanEvent> = NoOpEventMapper(),
    private val serializer: ContextAwareSerializer<SpanEvent>,
    private val internalLogger: InternalLogger,
    private val rumContextPropagator: RumContextPropagator = RumContextPropagator { sdkCore }
) : Writer {

    // region Writer
    override fun start() {
        // NO - OP
    }

    override fun write(trace: List<DDSpan>?) {
        if (trace == null) return
        sdkCore.getFeature(Feature.TRACING_FEATURE_NAME)
            ?.withWriteContext { ooContext, writeScope ->
                val writeSpans = trace
                    .filter { it.getTraceSamplingPriority() !in DROP_SAMPLING_PRIORITIES }
                    .map { it.extractRumContext(rumContextPropagator) }
                // TODO RUM-4092 Add the capability in the serializer to handle multiple spans in one payload
                writeScope {
                    writeSpans
                        .forEach { span ->
                            @Suppress("ThreadSafety") // called in the worker context
                            writeSpan(ooContext, it, span)
                        }
                }
            }
    }

    override fun flush(): Boolean {
        // NO - OP
        return true
    }

    override fun incrementDropCounts(p0: Int) {
        // NO - OP
    }

    override fun close() {
        // NO - OP
    }
    // endregion

    @WorkerThread
    private fun writeSpan(
        ooContext: OpenObserveContext,
        writer: EventBatchWriter,
        span: DDSpan
    ) {
        val spanEvent = ddSpanToSpanEventMapper.map(ooContext, span)
        val mapped = eventMapper.map(spanEvent) ?: return
        try {
            val serialized = serializer
                .serialize(ooContext, mapped)
                ?.toByteArray(Charsets.UTF_8) ?: return
            synchronized(this) {
                writer.write(RawBatchEvent(data = serialized), batchMetadata = null, eventType = EventType.DEFAULT)
            }
        } catch (@Suppress("TooGenericExceptionCaught") e: Throwable) {
            internalLogger.log(
                InternalLogger.Level.ERROR,
                listOf(InternalLogger.Target.USER, InternalLogger.Target.TELEMETRY),
                { ERROR_SERIALIZING.format(Locale.US, mapped.javaClass.simpleName) },
                e
            )
        }
    }

    companion object {
        internal const val ERROR_SERIALIZING = "Error serializing %s model"
        internal val DROP_SAMPLING_PRIORITIES = setOf(
            PrioritySampling.SAMPLER_DROP.toInt(),
            PrioritySampling.USER_DROP.toInt()
        )
    }
}
