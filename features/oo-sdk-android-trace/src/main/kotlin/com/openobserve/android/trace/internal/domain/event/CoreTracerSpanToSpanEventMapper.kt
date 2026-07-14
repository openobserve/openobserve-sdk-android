/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.trace.internal.domain.event

import com.openobserve.android.api.context.OpenObserveContext
import com.openobserve.android.log.LogAttributes
import com.openobserve.android.trace.model.SpanEvent
import com.openobserve.trace.api.DDSpanId
import com.openobserve.trace.api.internal.util.LongStringUtils
import com.openobserve.trace.api.sampling.PrioritySampling
import com.openobserve.trace.bootstrap.instrumentation.api.AgentSpanLink
import com.openobserve.trace.core.DDSpan
import com.openobserve.trace.core.DDSpanContext
import com.google.gson.JsonArray
import com.google.gson.JsonObject

@Suppress("TooManyFunctions")
internal class CoreTracerSpanToSpanEventMapper(
    internal val networkInfoEnabled: Boolean
) : BaseSpanEventMapper<DDSpan>() {

    // region Mapper

    override fun map(ooContext: OpenObserveContext, model: DDSpan): SpanEvent {
        val serverOffset = ooContext.time.serverTimeOffsetNs
        val metrics = resolveMetrics(model)
        val metadata = resolveMeta(ooContext, model)
        val lessSignificantTraceId = LongStringUtils.toHexStringPadded(model.traceId.toLong(), TRACE_ID_HEXA_SIZE)
        return SpanEvent(
            traceId = lessSignificantTraceId,
            spanId = resolveSpanId(model),
            parentId = resolveParentId(model),
            resource = model.resourceName.toString(),
            name = model.operationName.toString(), // GET, POST, etc
            service = model.serviceName,
            duration = model.durationNano,
            start = model.startTime + serverOffset,
            error = model.error.toLong(),
            meta = metadata,
            metrics = metrics
        )
    }

    // endregion

    // region internal

    private fun resolveSpanId(model: DDSpan): String {
        // the span id is always 64 bits long so we can pad it with zeros
        return DDSpanId.toHexStringPadded(model.spanId)
    }

    private fun resolveParentId(model: DDSpan): String {
        return DDSpanId.toHexStringPadded(model.parentId)
    }

    private fun resolveMetrics(event: DDSpan): SpanEvent.Metrics {
        val metrics = resolveMetricsFromSpanContext(event).apply {
            val spanSamplingPriority = event.spanSamplingPriority
            if (spanSamplingPriority != PrioritySampling.UNSET.toInt()) {
                // This required for backward compatibility with AndroidTracer that
                // don't add the sampling priority if it not set for current span.
                this[DDSpanContext.PRIORITY_SAMPLING_KEY] = spanSamplingPriority
            }
        }
        return SpanEvent.Metrics(
            topLevel = if (event.parentId == 0L) 1 else null,
            additionalProperties = metrics
        )
    }

    private fun resolveMeta(ooContext: OpenObserveContext, event: DDSpan): SpanEvent.Meta {
        val deviceInfo = resolveDeviceInfo(ooContext.deviceInfo)
        val osInfo = resolveOsInfo(ooContext.deviceInfo)
        val networkInfoMeta = if (networkInfoEnabled) resolveNetworkInfo(ooContext.networkInfo) else null
        val userInfo = ooContext.userInfo
        val accountInfo = ooContext.accountInfo
        val usrMeta = SpanEvent.Usr(
            id = userInfo.id,
            name = userInfo.name,
            email = userInfo.email,
            additionalProperties = userInfo.additionalProperties.toMutableMap()
        )
        val accountMeta = accountInfo?.let { resolveAccountInfo(it) }
        val dd = SpanEvent.Dd(
            source = ooContext.source,
            application = event.tags[LogAttributes.RUM_APPLICATION_ID]?.let { SpanEvent.Application(it as? String) },
            session = event.tags[LogAttributes.RUM_SESSION_ID]?.let { SpanEvent.Session(it as? String) },
            view = event.tags[LogAttributes.RUM_VIEW_ID]?.let { SpanEvent.View(it as? String) }
        )
        val mostSignificantTraceId =
            LongStringUtils.toHexStringPadded(event.traceId.toHighOrderLong(), TRACE_ID_HEXA_SIZE)
        val tags = event.tags.mapValues { it.value.toString() }
        val meta = mutableMapOf<String, String>()
        meta.putAll(event.baggage)
        meta.putAll(tags)
        meta[TRACE_ID_META_KEY] = mostSignificantTraceId
        meta[APPLICATION_VARIANT_KEY] = ooContext.variant
        resolveSpanLinks(event)?.let { meta[SPAN_LINKS_KEY] = it }
        return SpanEvent.Meta(
            version = ooContext.version,
            dd = dd,
            span = SpanEvent.Span(),
            tracer = SpanEvent.Tracer(
                version = ooContext.sdkVersion
            ),
            usr = usrMeta,
            account = accountMeta,
            network = networkInfoMeta,
            device = deviceInfo,
            os = osInfo,
            additionalProperties = meta
        )
    }

    private fun resolveSpanLinks(model: DDSpan): String? {
        if (model.links.isEmpty()) return null
        return model.links.map { resolveSpanLink(it) }.fold(JsonArray()) { acc, link ->
            acc.add(link)
            acc
        }.toString()
    }

    private fun resolveSpanLink(link: AgentSpanLink): JsonObject {
        // The SpanLinks support the full 128 bits trace so we can use the full hex string
        val linkedTraceId = link.traceId().toHexString()
        val linkedSpanId = DDSpanId.toHexStringPadded(link.spanId())
        val attributes = toJson(link.attributes().asMap())
        val flags = link.traceFlags()
        val traceState = link.traceState()
        val spanLink = JsonObject().apply {
            addProperty(TRACE_ID_KEY, linkedTraceId)
            addProperty(SPAN_ID_KEY, linkedSpanId)
            add(ATTRIBUTES_KEY, attributes)
            if (flags.toInt() != 0) {
                addProperty(FLAGS_KEY, flags)
            }
            if (traceState.isNotEmpty()) {
                addProperty(TRACE_STATE_KEY, traceState)
            }
        }
        return spanLink
    }

    private fun resolveMetricsFromSpanContext(span: DDSpan): MutableMap<String, Number> {
        return span.tags
            .filterValues { it is Number }
            .mapValues { it.value as Number }
            .toMutableMap()
    }

    private fun toJson(map: Map<String, String>): JsonObject {
        val jsonObject = JsonObject()
        map.forEach { (key, value) ->
            jsonObject.addProperty(key, value)
        }
        return jsonObject
    }

    // endregion

    companion object {
        private const val TRACE_ID_HEXA_SIZE = 16
        private const val ATTRIBUTES_KEY = "attributes"
        private const val SPAN_ID_KEY = "span_id"
        private const val TRACE_ID_KEY = "trace_id"
        private const val TRACE_STATE_KEY = "tracestate"
        private const val FLAGS_KEY = "flags"
        internal const val SPAN_LINKS_KEY = "_oo.span_links"
    }
}
