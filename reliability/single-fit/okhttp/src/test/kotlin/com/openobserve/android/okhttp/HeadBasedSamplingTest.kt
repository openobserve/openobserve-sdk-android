/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */
@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package com.openobserve.android.okhttp

import com.openobserve.android.OpenObserve
import com.openobserve.android.api.SdkCore
import com.openobserve.android.api.feature.Feature
import com.openobserve.android.core.stub.StubSDKCore
import com.openobserve.android.internal.utils.toHexString
import com.openobserve.android.okhttp.otel.addParentSpan
import com.openobserve.android.okhttp.tests.assertj.SpansPayloadAssert
import com.openobserve.android.okhttp.tests.elmyr.OkHttpConfigurator
import com.openobserve.android.okhttp.trace.TracingInterceptor
import com.openobserve.android.rum.ExperimentalRumApi
import com.openobserve.android.tests.ktx.getString
import com.openobserve.android.trace.ApmNetworkInstrumentationConfiguration
import com.openobserve.android.trace.OpenObserveTracing
import com.openobserve.android.trace.ExperimentalTraceApi
import com.openobserve.android.trace.GlobalOpenObserveTracer
import com.openobserve.android.trace.Trace
import com.openobserve.android.trace.TraceConfiguration
import com.openobserve.android.trace.TraceContextInjection
import com.openobserve.android.trace.TracingHeaderType
import com.openobserve.android.trace.api.OpenObserveTracingConstants
import com.openobserve.android.trace.api.span.OpenObserveSpan
import com.openobserve.android.trace.internal._TraceInternalProxy
import com.openobserve.android.trace.opentelemetry.OtelTracerProvider
import com.openobserve.tools.unit.extensions.TestConfigurationExtension
import com.openobserve.tools.unit.getFieldValue
import com.openobserve.tools.unit.getStaticValue
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.annotation.StringForgery
import fr.xgouchet.elmyr.junit5.ForgeConfiguration
import fr.xgouchet.elmyr.junit5.ForgeExtension
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.quality.Strictness

@Extensions(
    ExtendWith(MockitoExtension::class),
    ExtendWith(ForgeExtension::class),
    ExtendWith(TestConfigurationExtension::class)
)
@ForgeConfiguration(OkHttpConfigurator::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HeadBasedSamplingTest {

    private lateinit var stubSdkCore: StubSDKCore
    private lateinit var mockServer: MockWebServer

    @BeforeEach
    fun `set up`(forge: Forge) {
        stubSdkCore = StubSDKCore(forge)
        val registry: Any = OpenObserve::class.java.getStaticValue("registry")
        val instances: MutableMap<String, SdkCore> = registry.getFieldValue("instances")
        instances += stubSdkCore.name to stubSdkCore
        mockServer = MockWebServer()

        val fakeTraceConfiguration = TraceConfiguration.Builder().build()
        Trace.enable(fakeTraceConfiguration, stubSdkCore)
    }

    @AfterEach
    fun `tear down`() {
        unregisterGlobalTracer()
        OpenObserve.stopInstance(stubSdkCore.name)
        mockServer.shutdown()
    }

    @Test
    fun `M use network sampling rate W call is made { no parent context }`(forge: Forge) {
        // Given
        if (forge.aBool()) {
            // outcome should be the same for cases where there is GlobalTracer and when local tracer is used
            registerGlobalTracer(0.0)
        }

        mockServer.enqueue(MockResponse())
        mockServer.start()
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(
                TracingInterceptor.Builder(
                    tracedHostsWithHeaderType = mapOf(mockServer.hostName to setOf(TracingHeaderType.OPENOBSERVE))
                )
                    .setTraceContextInjection(TraceContextInjection.ALL)
                    .setSdkInstanceName(stubSdkCore.name)
                    .setTraceSampleRate(100f)
                    .build()
            )
            .build()

        // When
        okHttpClient.newCall(
            Request.Builder()
                .url(mockServer.url("/"))
                .build()
        ).execute()

        // Then
        val requestSent = mockServer.takeRequest()
        assertThat(requestSent.getHeader(OPENOBSERVE_SAMPLING_PRIORITY_HEADER)).isEqualTo("1")
        val leastSignificantTraceId = requestSent.getHeader(OPENOBSERVE_TRACE_ID_HEADER)
        checkNotNull(leastSignificantTraceId)
        val spanId = requestSent.getHeader(OPENOBSERVE_SPAN_ID_HEADER)
        checkNotNull(spanId)
        val ooTags = requestSent.getHeader(OPENOBSERVE_TAGS_HEADER)
            ?.toTags()
        checkNotNull(ooTags)
        assertThat(ooTags).isNotEmpty
        assertThat(ooTags).containsKey(MOST_SIGNIFICANT_TRACE_ID_KEY)
        val mostSignificantTraceId = ooTags.getValue(MOST_SIGNIFICANT_TRACE_ID_KEY)

        val eventsWritten = stubSdkCore.eventsWritten(Feature.TRACING_FEATURE_NAME)
        assertThat(eventsWritten).hasSize(1)

        val spanPayload = JsonParser.parseString(eventsWritten[0].eventData) as JsonObject
        SpansPayloadAssert.assertThat(spanPayload)
            .hasEnv(stubSdkCore.getOpenObserveContext().env)
            .hasSpanAtIndexWith(0) {
                hasLeastSignificant64BitsTraceId(
                    leastSignificantTraceId.toLong().toHexString().padStart(16, '0')
                )
                hasMostSignificant64BitsTraceId(mostSignificantTraceId)
                hasSpanId(spanId.toHexPaddedFromDecimalString())
                hasVersion(stubSdkCore.getOpenObserveContext().version)
                hasSource(stubSdkCore.getOpenObserveContext().source)
                hasTracerVersion(stubSdkCore.getOpenObserveContext().sdkVersion)
                hasError(0)
                hasName("okhttp.request")
                hasResource("http://${mockServer.hostName}:${mockServer.port}/")
                hasAgentPsr(1.0)
                hasSamplingPriority(1)
                hasGenericMetricValue("_top_level", 1)
                hasSpanKind("client")
                hasHttpMethod("GET")
                hasHttpUrl("http://${mockServer.hostName}:${mockServer.port}/")
                hasHttpStatusCode(200)
            }
    }

    @Test
    fun `M calculate PSR rate W call is made { no parent context }`(forge: Forge) {
        // Given
        if (forge.aBool()) {
            // outcome should be the same for cases where there is GlobalTracer and when local tracer is used
            registerGlobalTracer(0.0)
        }

        mockServer.start()
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(
                TracingInterceptor.Builder(
                    tracedHostsWithHeaderType = mapOf(mockServer.hostName to setOf(TracingHeaderType.OPENOBSERVE))
                )
                    .setTraceContextInjection(TraceContextInjection.ALL)
                    .setSdkInstanceName(stubSdkCore.name)
                    .setTraceSampleRate(50f)
                    .build()
            )
            .build()

        // When

        repeat(10) {
            mockServer.enqueue(MockResponse())
            okHttpClient.newCall(
                Request.Builder()
                    .url(mockServer.url("/"))
                    .build()
            ).execute()
        }

        // Then
        val eventsWritten = stubSdkCore.eventsWritten(Feature.TRACING_FEATURE_NAME)
        eventsWritten.forEach {
            val spanPayload = JsonParser.parseString(eventsWritten[0].eventData) as JsonObject
            SpansPayloadAssert.assertThat(spanPayload)
                .hasEnv(stubSdkCore.getOpenObserveContext().env)
                .hasSpanAtIndexWith(0) {
                    hasAgentPsr(0.5)
                }
        }
    }

    @Test
    fun `M respect parent sampling decision W call is made {parent context OpenObserveTracing Span, parent not sampled}`(
        @StringForgery fakeSpanName: String
    ) {
        // Given
        registerGlobalTracer(0.0)

        mockServer.enqueue(MockResponse())
        mockServer.start()
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(
                TracingInterceptor.Builder(
                    tracedHostsWithHeaderType = mapOf(mockServer.hostName to setOf(TracingHeaderType.OPENOBSERVE))
                )
                    .setTraceContextInjection(TraceContextInjection.ALL)
                    .setSdkInstanceName(stubSdkCore.name)
                    .setTraceSampleRate(100f)
                    .build()
            )
            .build()

        val parentSpan = GlobalOpenObserveTracer.get()
            .buildSpan(fakeSpanName)
            .start()

        // When
        okHttpClient.newCall(
            Request.Builder()
                .url(mockServer.url("/"))
                .parentSpan(parentSpan)
                .build()
        ).execute()
        parentSpan.finish()

        // Then
        val requestSent = mockServer.takeRequest()
        assertThat(requestSent.getHeader(OPENOBSERVE_SAMPLING_PRIORITY_HEADER)).isEqualTo("0")
        assertThat(requestSent.getHeader(OPENOBSERVE_TRACE_ID_HEADER)).isNotEmpty()
        assertThat(requestSent.getHeader(OPENOBSERVE_SPAN_ID_HEADER)).isNotEmpty()
        assertThat(requestSent.getHeader(OPENOBSERVE_TAGS_HEADER)).isNotEmpty()

        val eventsWritten = stubSdkCore.eventsWritten(Feature.TRACING_FEATURE_NAME)
        assertThat(eventsWritten).isEmpty()
    }

    @Test
    fun `M respect parent sampling decision W call is made { parent context = OpenTelemetry Span, parent not sampled }`(
        @StringForgery fakeInstrumentationName: String,
        @StringForgery fakeSpanName: String
    ) {
        // Given
        val otelTracer = OtelTracerProvider.Builder(stubSdkCore)
            .setTracingHeaderTypes(setOf(TracingHeaderType.OPENOBSERVE))
            .setSampleRate(0.0)
            .build()

        mockServer.enqueue(MockResponse())
        mockServer.start()
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(
                TracingInterceptor.Builder(
                    tracedHostsWithHeaderType = mapOf(mockServer.hostName to setOf(TracingHeaderType.OPENOBSERVE))
                )
                    .setTraceContextInjection(TraceContextInjection.ALL)
                    .setSdkInstanceName(stubSdkCore.name)
                    .setTraceSampleRate(100f)
                    .build()
            )
            .build()

        val parentSpan = otelTracer.get(fakeInstrumentationName)
            .spanBuilder(fakeSpanName)
            .startSpan()

        // When
        okHttpClient.newCall(
            Request.Builder()
                .url(mockServer.url("/"))
                .addParentSpan(parentSpan)
                .build()
        ).execute()
        parentSpan.end()

        // Then
        val requestSent = mockServer.takeRequest()
        assertThat(requestSent.getHeader(OPENOBSERVE_SAMPLING_PRIORITY_HEADER)).isEqualTo("0")
        assertThat(requestSent.getHeader(OPENOBSERVE_TRACE_ID_HEADER)).isNotEmpty()
        assertThat(requestSent.getHeader(OPENOBSERVE_SPAN_ID_HEADER)).isNotEmpty()
        assertThat(requestSent.getHeader(OPENOBSERVE_TAGS_HEADER)).isNotEmpty()

        val eventsWritten = stubSdkCore.eventsWritten(Feature.TRACING_FEATURE_NAME)
        assertThat(eventsWritten).isEmpty()
    }

    @Test
    fun `M respect parent sampling decision W call is made { parent context = OpenObserveTracing Span, parent sampled }`(
        @StringForgery fakeSpanName: String
    ) {
        // Given
        registerGlobalTracer(100.0)

        mockServer.enqueue(MockResponse())
        mockServer.start()
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(
                TracingInterceptor.Builder(
                    tracedHostsWithHeaderType = mapOf(mockServer.hostName to setOf(TracingHeaderType.OPENOBSERVE))
                )
                    .setTraceContextInjection(TraceContextInjection.ALL)
                    .setSdkInstanceName(stubSdkCore.name)
                    .setTraceSampleRate(0f)
                    .build()
            )
            .build()

        val parentSpan = GlobalOpenObserveTracer.get()
            .buildSpan(fakeSpanName)
            .start()

        // When
        okHttpClient.newCall(
            Request.Builder()
                .url(mockServer.url("/"))
                .parentSpan(parentSpan)
                .build()
        ).execute()
        parentSpan.finish()

        // Then
        val requestSent = mockServer.takeRequest()
        assertThat(requestSent.getHeader(OPENOBSERVE_SAMPLING_PRIORITY_HEADER)).isEqualTo("1")
        val leastSignificantTraceId = requestSent.getHeader(OPENOBSERVE_TRACE_ID_HEADER)
        checkNotNull(leastSignificantTraceId)
        val spanId = requestSent.getHeader(OPENOBSERVE_SPAN_ID_HEADER)
        checkNotNull(spanId)
        val ooTags = requestSent.getHeader(OPENOBSERVE_TAGS_HEADER)
            ?.toTags()
        checkNotNull(ooTags)
        assertThat(ooTags).isNotEmpty
        assertThat(ooTags).containsKey(MOST_SIGNIFICANT_TRACE_ID_KEY)
        val mostSignificantTraceId = ooTags.getValue(MOST_SIGNIFICANT_TRACE_ID_KEY)

        val eventsWritten = stubSdkCore.eventsWritten(Feature.TRACING_FEATURE_NAME)
        assertThat(eventsWritten).hasSize(2)

        val localSpanPayload = JsonParser.parseString(eventsWritten[0].eventData).asJsonObject
        SpansPayloadAssert.assertThat(localSpanPayload)
            .hasSpanAtIndexWith(0) {
                hasLeastSignificant64BitsTraceId(
                    leastSignificantTraceId.toLong().toHexString().padStart(16, '0')
                )
                hasMostSignificant64BitsTraceId(mostSignificantTraceId)
                hasParentId("0000000000000000")
                hasAgentPsr(1.0)
                hasSamplingPriority(OpenObserveTracingConstants.PrioritySampling.SAMPLER_KEEP)
                hasGenericMetricValue("_top_level", 1)
            }

        val localSpanId = getLocalSpanId(localSpanPayload)

        val okHttpSpanPayload = JsonParser.parseString(eventsWritten[1].eventData) as JsonObject
        SpansPayloadAssert.assertThat(okHttpSpanPayload)
            .hasEnv(stubSdkCore.getOpenObserveContext().env)
            .hasSpanAtIndexWith(0) {
                hasLeastSignificant64BitsTraceId(
                    leastSignificantTraceId.toLong().toHexString().padStart(16, '0')
                )
                hasMostSignificant64BitsTraceId(mostSignificantTraceId)
                hasSpanId(spanId.toHexPaddedFromDecimalString())
                hasParentId(localSpanId.toHexPaddedFromHexString())
                hasVersion(stubSdkCore.getOpenObserveContext().version)
                hasSource(stubSdkCore.getOpenObserveContext().source)
                hasTracerVersion(stubSdkCore.getOpenObserveContext().sdkVersion)
                hasError(0)
                hasName("okhttp.request")
                hasResource("http://${mockServer.hostName}:${mockServer.port}/")
                hasNoAgentPsr()
                hasNoSamplingPriority()
                hasNoGenericMetric("_top_level")
                hasSpanKind("client")
                hasHttpMethod("GET")
                hasHttpUrl("http://${mockServer.hostName}:${mockServer.port}/")
                hasHttpStatusCode(200)
            }
    }

    @Test
    fun `M respect parent sampling decision W call is made { parent context = OpenTelemetry Span, parent sampled }`(
        @StringForgery fakeInstrumentationName: String,
        @StringForgery fakeSpanName: String
    ) {
        // Given
        val otelTracerProvider = OtelTracerProvider.Builder(stubSdkCore)
            .setTracingHeaderTypes(setOf(TracingHeaderType.OPENOBSERVE))
            .setSampleRate(100.0)
            .build()

        mockServer.enqueue(MockResponse())
        mockServer.start()
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(
                TracingInterceptor.Builder(
                    tracedHostsWithHeaderType = mapOf(mockServer.hostName to setOf(TracingHeaderType.OPENOBSERVE))
                )
                    .setTraceContextInjection(TraceContextInjection.ALL)
                    .setSdkInstanceName(stubSdkCore.name)
                    .setTraceSampleRate(0f)
                    .build()
            )
            .build()

        val parentSpan = otelTracerProvider.get(fakeInstrumentationName)
            .spanBuilder(fakeSpanName)
            .startSpan()

        // When
        okHttpClient.newCall(
            Request.Builder()
                .url(mockServer.url("/"))
                .addParentSpan(parentSpan)
                .build()
        ).execute()
        parentSpan.end()

        // Then
        val requestSent = mockServer.takeRequest()
        assertThat(requestSent.getHeader(OPENOBSERVE_SAMPLING_PRIORITY_HEADER)).isEqualTo("2")
        val leastSignificantTraceId = requestSent.getHeader(OPENOBSERVE_TRACE_ID_HEADER)
        checkNotNull(leastSignificantTraceId)
        val spanId = requestSent.getHeader(OPENOBSERVE_SPAN_ID_HEADER)
        checkNotNull(spanId)
        val ooTags = requestSent.getHeader(OPENOBSERVE_TAGS_HEADER)
            ?.toTags()
        checkNotNull(ooTags)
        assertThat(ooTags).isNotEmpty
        assertThat(ooTags).containsKey(MOST_SIGNIFICANT_TRACE_ID_KEY)
        val mostSignificantTraceId = ooTags.getValue(MOST_SIGNIFICANT_TRACE_ID_KEY)

        val eventsWritten = stubSdkCore.eventsWritten(Feature.TRACING_FEATURE_NAME)
        assertThat(eventsWritten).hasSize(2)

        val localSpanPayload = JsonParser.parseString(eventsWritten[1].eventData).asJsonObject
        SpansPayloadAssert.assertThat(localSpanPayload)
            .hasSpanAtIndexWith(0) {
                hasLeastSignificant64BitsTraceId(
                    leastSignificantTraceId.toLong().toHexString().padStart(16, '0')
                )
                hasMostSignificant64BitsTraceId(mostSignificantTraceId)
                hasParentId("0000000000000000")
                // OpenTelemetry span will have _oo.rule_psr instead of _oo.agent_psr
                hasRulePsr(1.0)
                hasSamplingPriority(OpenObserveTracingConstants.PrioritySampling.USER_KEEP)
                hasGenericMetricValue("_top_level", 1)
            }

        val localSpanId = getLocalSpanId(localSpanPayload)

        val okHttpSpanPayload = JsonParser.parseString(eventsWritten[0].eventData) as JsonObject
        SpansPayloadAssert.assertThat(okHttpSpanPayload)
            .hasEnv(stubSdkCore.getOpenObserveContext().env)
            .hasSpanAtIndexWith(0) {
                hasLeastSignificant64BitsTraceId(
                    leastSignificantTraceId.toLong().toHexString().padStart(16, '0')
                )
                hasMostSignificant64BitsTraceId(mostSignificantTraceId)
                hasSpanId(spanId.toHexPaddedFromDecimalString())
                hasParentId(localSpanId.toHexPaddedFromHexString())
                hasVersion(stubSdkCore.getOpenObserveContext().version)
                hasSource(stubSdkCore.getOpenObserveContext().source)
                hasTracerVersion(stubSdkCore.getOpenObserveContext().sdkVersion)
                hasError(0)
                hasName("okhttp.request")
                hasResource("http://${mockServer.hostName}:${mockServer.port}/")
                hasNoAgentPsr()
                hasSamplingPriority(OpenObserveTracingConstants.PrioritySampling.USER_KEEP)
                hasNoGenericMetric("_top_level")
                hasSpanKind("client")
                hasHttpMethod("GET")
                hasHttpUrl("http://${mockServer.hostName}:${mockServer.port}/")
                hasHttpStatusCode(200)
            }
    }

    @Test
    fun `M respect parent sampling decision W call is made {parent context = headers, parent not sampled}`(
        @StringForgery fakeSpanName: String
    ) {
        // Given
        registerGlobalTracer(0.0)

        mockServer.enqueue(MockResponse())
        mockServer.start()
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(
                TracingInterceptor.Builder(
                    tracedHostsWithHeaderType = mapOf(mockServer.hostName to setOf(TracingHeaderType.OPENOBSERVE))
                )
                    .setTraceContextInjection(TraceContextInjection.ALL)
                    .setSdkInstanceName(stubSdkCore.name)
                    .setTraceSampleRate(100f)
                    .build()
            )
            .build()

        val parentSpan = GlobalOpenObserveTracer.get()
            .buildSpan(fakeSpanName)
            .start()

        // When
        okHttpClient.newCall(
            Request.Builder()
                .url(mockServer.url("/"))
                .apply {
                    GlobalOpenObserveTracer.get().propagate().inject(
                        context = parentSpan.context(),
                        carrier = this
                    ) { carrier, key, value -> carrier.addHeader(key, value) }
                }
                .build()
        ).execute()
        parentSpan.finish()

        // Then
        val requestSent = mockServer.takeRequest()
        assertThat(requestSent.getHeader(OPENOBSERVE_SAMPLING_PRIORITY_HEADER)).isEqualTo("0")
        assertThat(requestSent.getHeader(OPENOBSERVE_TRACE_ID_HEADER)).isNotEmpty()
        assertThat(requestSent.getHeader(OPENOBSERVE_SPAN_ID_HEADER)).isNotEmpty()
        assertThat(requestSent.getHeader(OPENOBSERVE_TAGS_HEADER)).isNotEmpty()

        val eventsWritten = stubSdkCore.eventsWritten(Feature.TRACING_FEATURE_NAME)
        assertThat(eventsWritten).isEmpty()
    }

    @Test
    fun `M respect parent sampling decision W call is made { parent context=headers, parent sampled }`(
        @StringForgery fakeSpanName: String
    ) {
        // Given
        registerGlobalTracer(100.0)

        mockServer.enqueue(MockResponse())
        mockServer.start()
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(
                TracingInterceptor.Builder(
                    tracedHostsWithHeaderType = mapOf(mockServer.hostName to setOf(TracingHeaderType.OPENOBSERVE))
                )
                    .setTraceContextInjection(TraceContextInjection.ALL)
                    .setSdkInstanceName(stubSdkCore.name)
                    .setTraceSampleRate(100f)
                    .build()
            )
            .build()

        val parentSpan = GlobalOpenObserveTracer.get()
            .buildSpan(fakeSpanName)
            .start()

        // When
        okHttpClient.newCall(
            Request.Builder()
                .url(mockServer.url("/"))
                .apply {
                    GlobalOpenObserveTracer.get()
                        .propagate()
                        .inject(parentSpan.context(), this) { _, key, value ->
                            addHeader(key, value)
                        }
                }
                .build()
        ).execute()
        parentSpan.finish()

        // Then
        val requestSent = mockServer.takeRequest()
        assertThat(requestSent.getHeader(OPENOBSERVE_SAMPLING_PRIORITY_HEADER)).isEqualTo("1")
        val leastSignificantTraceId = requestSent.getHeader(OPENOBSERVE_TRACE_ID_HEADER)
        checkNotNull(leastSignificantTraceId)
        val spanId = requestSent.getHeader(OPENOBSERVE_SPAN_ID_HEADER)
        checkNotNull(spanId)
        val ooTags = requestSent.getHeader(OPENOBSERVE_TAGS_HEADER)
            ?.toTags()
        checkNotNull(ooTags)
        assertThat(ooTags).isNotEmpty
        assertThat(ooTags).containsKey(MOST_SIGNIFICANT_TRACE_ID_KEY)
        val mostSignificantTraceId = ooTags.getValue(MOST_SIGNIFICANT_TRACE_ID_KEY)

        val eventsWritten = stubSdkCore.eventsWritten(Feature.TRACING_FEATURE_NAME)
        assertThat(eventsWritten).hasSize(2)

        val localSpanPayload = JsonParser.parseString(eventsWritten[1].eventData).asJsonObject
        SpansPayloadAssert.assertThat(localSpanPayload)
            .hasSpanAtIndexWith(0) {
                hasLeastSignificant64BitsTraceId(
                    leastSignificantTraceId.toLong().toHexString().padStart(16, '0')
                )
                hasMostSignificant64BitsTraceId(mostSignificantTraceId)
                hasParentId("0000000000000000")
                hasAgentPsr(1.0)
                hasSamplingPriority(OpenObserveTracingConstants.PrioritySampling.SAMPLER_KEEP)
                hasGenericMetricValue("_top_level", 1)
            }

        val localSpanId = getLocalSpanId(localSpanPayload)

        val okHttpSpanPayload = JsonParser.parseString(eventsWritten[0].eventData) as JsonObject
        SpansPayloadAssert.assertThat(okHttpSpanPayload)
            .hasEnv(stubSdkCore.getOpenObserveContext().env)
            .hasSpanAtIndexWith(0) {
                hasLeastSignificant64BitsTraceId(
                    leastSignificantTraceId.toLong().toHexString().padStart(16, '0')
                )
                hasMostSignificant64BitsTraceId(mostSignificantTraceId)
                hasSpanId(spanId.toHexPaddedFromDecimalString())
                hasParentId(localSpanId.toHexPaddedFromHexString())
                hasVersion(stubSdkCore.getOpenObserveContext().version)
                hasSource(stubSdkCore.getOpenObserveContext().source)
                hasTracerVersion(stubSdkCore.getOpenObserveContext().sdkVersion)
                hasError(0)
                hasName("okhttp.request")
                hasResource("http://${mockServer.hostName}:${mockServer.port}/")
                hasNoAgentPsr()
                // this one will have sampling priority unlike in case of propagation with tagged Span directly,
                // because there sampling priority is not yet set at the parent during child creation
                hasSamplingPriority(1)
                hasNoGenericMetric("_top_level")
                hasSpanKind("client")
                hasHttpMethod("GET")
                hasHttpUrl("http://${mockServer.hostName}:${mockServer.port}/")
                hasHttpStatusCode(200)
            }
    }

    private fun getLocalSpanId(localSpanPayload: JsonObject): String {
        return localSpanPayload.getAsJsonArray("spans")
            .first()
            .asJsonObject
            .getString("span_id")
            .orEmpty()
    }

    private fun registerGlobalTracer(sampleRate: Double) {
        GlobalOpenObserveTracer.registerIfAbsent(
            OpenObserveTracing.newTracerBuilder(stubSdkCore)
                .withTracingHeadersTypes(setOf(TracingHeaderType.OPENOBSERVE))
                // this is on purpose, we want to make sure that it is not taken into account
                .withSampleRate(sampleRate)
                .build()
        )
    }

    private fun unregisterGlobalTracer() {
        GlobalOpenObserveTracer.clear()
    }

    // region new instrumentation API tests

    @Test
    fun `M use network sampling rate W call is made { no parent context, new instrumentation }`(forge: Forge) {
        // Given
        if (forge.aBool()) {
            registerGlobalTracer(0.0)
        }

        mockServer.enqueue(MockResponse())
        mockServer.start()
        val okHttpClient = buildInstrumentedClient(sampleRate = 100f)

        // When
        okHttpClient.newCall(
            Request.Builder()
                .url(mockServer.url("/"))
                .build()
        ).execute()

        // Then
        val requestSent = mockServer.takeRequest()
        assertThat(requestSent.getHeader(OPENOBSERVE_SAMPLING_PRIORITY_HEADER)).isEqualTo("1")
        val leastSignificantTraceId = requestSent.getHeader(OPENOBSERVE_TRACE_ID_HEADER)
        checkNotNull(leastSignificantTraceId)
        val spanId = requestSent.getHeader(OPENOBSERVE_SPAN_ID_HEADER)
        checkNotNull(spanId)
        val ooTags = requestSent.getHeader(OPENOBSERVE_TAGS_HEADER)
            ?.toTags()
        checkNotNull(ooTags)
        assertThat(ooTags).isNotEmpty
        assertThat(ooTags).containsKey(MOST_SIGNIFICANT_TRACE_ID_KEY)
        val mostSignificantTraceId = ooTags.getValue(MOST_SIGNIFICANT_TRACE_ID_KEY)

        val eventsWritten = stubSdkCore.eventsWritten(Feature.TRACING_FEATURE_NAME)
        assertThat(eventsWritten).hasSize(1)

        val spanPayload = JsonParser.parseString(eventsWritten[0].eventData) as JsonObject
        SpansPayloadAssert.assertThat(spanPayload)
            .hasEnv(stubSdkCore.getOpenObserveContext().env)
            .hasSpanAtIndexWith(0) {
                hasLeastSignificant64BitsTraceId(
                    leastSignificantTraceId.toLong().toHexString().padStart(16, '0')
                )
                hasMostSignificant64BitsTraceId(mostSignificantTraceId)
                hasSpanId(spanId.toHexPaddedFromDecimalString())
                hasVersion(stubSdkCore.getOpenObserveContext().version)
                hasSource(stubSdkCore.getOpenObserveContext().source)
                hasTracerVersion(stubSdkCore.getOpenObserveContext().sdkVersion)
                hasError(0)
                hasName("okhttp.request")
                hasResource("http://${mockServer.hostName}:${mockServer.port}/")
                hasAgentPsr(1.0)

                hasSamplingPriority(1)
                hasGenericMetricValue("_top_level", 1)
                hasSpanKind("client")
                hasHttpMethod("GET")
                hasHttpUrl("http://${mockServer.hostName}:${mockServer.port}/")
                hasHttpStatusCode(200)
            }
    }

    @Test
    fun `M calculate PSR rate W call is made { no parent context, new instrumentation }`(forge: Forge) {
        // Given
        if (forge.aBool()) {
            registerGlobalTracer(0.0)
        }

        mockServer.start()
        val okHttpClient = buildInstrumentedClient(sampleRate = 50f)

        // When
        repeat(10) {
            mockServer.enqueue(MockResponse())
            okHttpClient.newCall(
                Request.Builder()
                    .url(mockServer.url("/"))
                    .build()
            ).execute()
        }

        // Then
        val eventsWritten = stubSdkCore.eventsWritten(Feature.TRACING_FEATURE_NAME)
        eventsWritten.forEach {
            val spanPayload = JsonParser.parseString(eventsWritten[0].eventData) as JsonObject
            SpansPayloadAssert.assertThat(spanPayload)
                .hasEnv(stubSdkCore.getOpenObserveContext().env)
                .hasSpanAtIndexWith(0) {
                    hasAgentPsr(0.5)
                }
        }
    }

    @Test
    @Suppress("LongMethod")
    fun `M respect parent sampling decision W call is made {parent=OpenObserveTracing, not sampled, new}`(
        @StringForgery fakeSpanName: String
    ) {
        // Given
        registerGlobalTracer(0.0)

        mockServer.enqueue(MockResponse())
        mockServer.start()
        val okHttpClient = buildInstrumentedClient(sampleRate = 100f)

        val parentSpan = GlobalOpenObserveTracer.get()
            .buildSpan(fakeSpanName)
            .start()

        // When
        okHttpClient.newCall(
            Request.Builder()
                .url(mockServer.url("/"))
                .parentSpan(parentSpan)
                .build()
        ).execute()
        parentSpan.finish()

        // Then
        val requestSent = mockServer.takeRequest()
        assertThat(requestSent.getHeader(OPENOBSERVE_SAMPLING_PRIORITY_HEADER)).isEqualTo("0")
        assertThat(requestSent.getHeader(OPENOBSERVE_TRACE_ID_HEADER)).isNotEmpty()
        assertThat(requestSent.getHeader(OPENOBSERVE_SPAN_ID_HEADER)).isNotEmpty()
        assertThat(requestSent.getHeader(OPENOBSERVE_TAGS_HEADER)).isNotEmpty()

        val eventsWritten = stubSdkCore.eventsWritten(Feature.TRACING_FEATURE_NAME)
        assertThat(eventsWritten).isEmpty()
    }

    @Test
    @Suppress("LongMethod")
    fun `M respect parent sampling decision W call is made {parent=OTel, not sampled, new}`(
        @StringForgery fakeInstrumentationName: String,
        @StringForgery fakeSpanName: String
    ) {
        // Given
        val otelTracer = OtelTracerProvider.Builder(stubSdkCore)
            .setTracingHeaderTypes(setOf(TracingHeaderType.OPENOBSERVE))
            .setSampleRate(0.0)
            .build()

        mockServer.enqueue(MockResponse())
        mockServer.start()
        val okHttpClient = buildInstrumentedClient(sampleRate = 100f)

        val parentSpan = otelTracer.get(fakeInstrumentationName)
            .spanBuilder(fakeSpanName)
            .startSpan()

        // When
        okHttpClient.newCall(
            Request.Builder()
                .url(mockServer.url("/"))
                .addParentSpan(parentSpan)
                .build()
        ).execute()
        parentSpan.end()

        // Then
        val requestSent = mockServer.takeRequest()
        assertThat(requestSent.getHeader(OPENOBSERVE_SAMPLING_PRIORITY_HEADER)).isEqualTo("0")
        assertThat(requestSent.getHeader(OPENOBSERVE_TRACE_ID_HEADER)).isNotEmpty()
        assertThat(requestSent.getHeader(OPENOBSERVE_SPAN_ID_HEADER)).isNotEmpty()
        assertThat(requestSent.getHeader(OPENOBSERVE_TAGS_HEADER)).isNotEmpty()

        val eventsWritten = stubSdkCore.eventsWritten(Feature.TRACING_FEATURE_NAME)
        assertThat(eventsWritten).isEmpty()
    }

    @Test
    fun `M respect parent sampling decision W call is made {parent=OpenObserveTracing Span, sampled, new instrumentation}`(
        @StringForgery fakeSpanName: String
    ) {
        // Given
        registerGlobalTracer(100.0)

        mockServer.enqueue(MockResponse())
        mockServer.start()
        val okHttpClient = buildInstrumentedClient(sampleRate = 0f)

        val parentSpan = GlobalOpenObserveTracer.get()
            .buildSpan(fakeSpanName)
            .start()

        // When
        okHttpClient.newCall(
            Request.Builder()
                .url(mockServer.url("/"))
                .parentSpan(parentSpan)
                .build()
        ).execute()
        parentSpan.finish()

        // Then
        val requestSent = mockServer.takeRequest()
        assertThat(requestSent.getHeader(OPENOBSERVE_SAMPLING_PRIORITY_HEADER)).isEqualTo("1")
        val leastSignificantTraceId = requestSent.getHeader(OPENOBSERVE_TRACE_ID_HEADER)
        checkNotNull(leastSignificantTraceId)
        val spanId = requestSent.getHeader(OPENOBSERVE_SPAN_ID_HEADER)
        checkNotNull(spanId)
        val ooTags = requestSent.getHeader(OPENOBSERVE_TAGS_HEADER)
            ?.toTags()
        checkNotNull(ooTags)
        assertThat(ooTags).isNotEmpty
        assertThat(ooTags).containsKey(MOST_SIGNIFICANT_TRACE_ID_KEY)
        val mostSignificantTraceId = ooTags.getValue(MOST_SIGNIFICANT_TRACE_ID_KEY)

        val eventsWritten = stubSdkCore.eventsWritten(Feature.TRACING_FEATURE_NAME)
        assertThat(eventsWritten).hasSize(2)

        val localSpanPayload = JsonParser.parseString(eventsWritten[0].eventData).asJsonObject
        SpansPayloadAssert.assertThat(localSpanPayload)
            .hasSpanAtIndexWith(0) {
                hasLeastSignificant64BitsTraceId(
                    leastSignificantTraceId.toLong().toHexString().padStart(16, '0')
                )
                hasMostSignificant64BitsTraceId(mostSignificantTraceId)
                hasParentId("0000000000000000")
                hasAgentPsr(1.0)
                hasSamplingPriority(OpenObserveTracingConstants.PrioritySampling.SAMPLER_KEEP)
                hasGenericMetricValue("_top_level", 1)
            }

        val localSpanId = getLocalSpanId(localSpanPayload)

        val okHttpSpanPayload = JsonParser.parseString(eventsWritten[1].eventData) as JsonObject
        SpansPayloadAssert.assertThat(okHttpSpanPayload)
            .hasEnv(stubSdkCore.getOpenObserveContext().env)
            .hasSpanAtIndexWith(0) {
                hasLeastSignificant64BitsTraceId(
                    leastSignificantTraceId.toLong().toHexString().padStart(16, '0')
                )
                hasMostSignificant64BitsTraceId(mostSignificantTraceId)
                hasSpanId(spanId.toHexPaddedFromDecimalString())
                hasParentId(localSpanId.toHexPaddedFromHexString())
                hasVersion(stubSdkCore.getOpenObserveContext().version)
                hasSource(stubSdkCore.getOpenObserveContext().source)
                hasTracerVersion(stubSdkCore.getOpenObserveContext().sdkVersion)
                hasError(0)
                hasName("okhttp.request")
                hasResource("http://${mockServer.hostName}:${mockServer.port}/")
                hasNoAgentPsr()
                hasNoSamplingPriority()
                hasNoGenericMetric("_top_level")
                hasSpanKind("client")
                hasHttpMethod("GET")
                hasHttpUrl("http://${mockServer.hostName}:${mockServer.port}/")
                hasHttpStatusCode(200)
            }
    }

    @Test
    fun `M respect parent sampling decision W call is made {parent=OpenTelemetry Span, sampled, new instrumentation}`(
        @StringForgery fakeInstrumentationName: String,
        @StringForgery fakeSpanName: String
    ) {
        // Given
        val otelTracerProvider = OtelTracerProvider.Builder(stubSdkCore)
            .setTracingHeaderTypes(setOf(TracingHeaderType.OPENOBSERVE))
            .setSampleRate(100.0)
            .build()

        mockServer.enqueue(MockResponse())
        mockServer.start()
        val okHttpClient = buildInstrumentedClient(sampleRate = 0f)

        val parentSpan = otelTracerProvider.get(fakeInstrumentationName)
            .spanBuilder(fakeSpanName)
            .startSpan()

        // When
        okHttpClient.newCall(
            Request.Builder()
                .url(mockServer.url("/"))
                .addParentSpan(parentSpan)
                .build()
        ).execute()
        parentSpan.end()

        // Then
        val requestSent = mockServer.takeRequest()
        assertThat(requestSent.getHeader(OPENOBSERVE_SAMPLING_PRIORITY_HEADER)).isEqualTo("2")
        val leastSignificantTraceId = requestSent.getHeader(OPENOBSERVE_TRACE_ID_HEADER)
        checkNotNull(leastSignificantTraceId)
        val spanId = requestSent.getHeader(OPENOBSERVE_SPAN_ID_HEADER)
        checkNotNull(spanId)
        val ooTags = requestSent.getHeader(OPENOBSERVE_TAGS_HEADER)
            ?.toTags()
        checkNotNull(ooTags)
        assertThat(ooTags).isNotEmpty
        assertThat(ooTags).containsKey(MOST_SIGNIFICANT_TRACE_ID_KEY)
        val mostSignificantTraceId = ooTags.getValue(MOST_SIGNIFICANT_TRACE_ID_KEY)

        val eventsWritten = stubSdkCore.eventsWritten(Feature.TRACING_FEATURE_NAME)
        assertThat(eventsWritten).hasSize(2)

        val localSpanPayload = JsonParser.parseString(eventsWritten[1].eventData).asJsonObject
        SpansPayloadAssert.assertThat(localSpanPayload)
            .hasSpanAtIndexWith(0) {
                hasLeastSignificant64BitsTraceId(
                    leastSignificantTraceId.toLong().toHexString().padStart(16, '0')
                )
                hasMostSignificant64BitsTraceId(mostSignificantTraceId)
                hasParentId("0000000000000000")
                // OpenTelemetry span will have _oo.rule_psr instead of _oo.agent_psr
                hasRulePsr(1.0)
                hasSamplingPriority(OpenObserveTracingConstants.PrioritySampling.USER_KEEP)
                hasGenericMetricValue("_top_level", 1)
            }

        val localSpanId = getLocalSpanId(localSpanPayload)

        val okHttpSpanPayload = JsonParser.parseString(eventsWritten[0].eventData) as JsonObject
        SpansPayloadAssert.assertThat(okHttpSpanPayload)
            .hasEnv(stubSdkCore.getOpenObserveContext().env)
            .hasSpanAtIndexWith(0) {
                hasLeastSignificant64BitsTraceId(
                    leastSignificantTraceId.toLong().toHexString().padStart(16, '0')
                )
                hasMostSignificant64BitsTraceId(mostSignificantTraceId)
                hasSpanId(spanId.toHexPaddedFromDecimalString())
                hasParentId(localSpanId.toHexPaddedFromHexString())
                hasVersion(stubSdkCore.getOpenObserveContext().version)
                hasSource(stubSdkCore.getOpenObserveContext().source)
                hasTracerVersion(stubSdkCore.getOpenObserveContext().sdkVersion)
                hasError(0)
                hasName("okhttp.request")
                hasResource("http://${mockServer.hostName}:${mockServer.port}/")
                hasNoAgentPsr()
                hasSamplingPriority(OpenObserveTracingConstants.PrioritySampling.USER_KEEP)
                hasNoGenericMetric("_top_level")
                hasSpanKind("client")
                hasHttpMethod("GET")
                hasHttpUrl("http://${mockServer.hostName}:${mockServer.port}/")
                hasHttpStatusCode(200)
            }
    }

    @Test
    fun `M respect parent sampling decision W call is made {parent=headers, not sampled, new instrumentation}`(
        @StringForgery fakeSpanName: String
    ) {
        // Given
        registerGlobalTracer(0.0)

        mockServer.enqueue(MockResponse())
        mockServer.start()
        val okHttpClient = buildInstrumentedClient(sampleRate = 100f)

        val parentSpan = GlobalOpenObserveTracer.get()
            .buildSpan(fakeSpanName)
            .start()

        // When
        okHttpClient.newCall(
            Request.Builder()
                .url(mockServer.url("/"))
                .apply {
                    GlobalOpenObserveTracer.get().propagate().inject(
                        context = parentSpan.context(),
                        carrier = this
                    ) { carrier, key, value -> carrier.addHeader(key, value) }
                }
                .build()
        ).execute()
        parentSpan.finish()

        // Then
        val requestSent = mockServer.takeRequest()
        assertThat(requestSent.getHeader(OPENOBSERVE_SAMPLING_PRIORITY_HEADER)).isEqualTo("0")
        assertThat(requestSent.getHeader(OPENOBSERVE_TRACE_ID_HEADER)).isNotEmpty()
        assertThat(requestSent.getHeader(OPENOBSERVE_SPAN_ID_HEADER)).isNotEmpty()
        assertThat(requestSent.getHeader(OPENOBSERVE_TAGS_HEADER)).isNotEmpty()

        val eventsWritten = stubSdkCore.eventsWritten(Feature.TRACING_FEATURE_NAME)
        assertThat(eventsWritten).isEmpty()
    }

    @Test
    fun `M respect parent sampling decision W call is made {parent=headers, sampled, new instrumentation}`(
        @StringForgery fakeSpanName: String
    ) {
        // Given
        registerGlobalTracer(100.0)

        mockServer.enqueue(MockResponse())
        mockServer.start()
        val okHttpClient = buildInstrumentedClient(sampleRate = 100f)

        val parentSpan = GlobalOpenObserveTracer.get()
            .buildSpan(fakeSpanName)
            .start()

        // When
        okHttpClient.newCall(
            Request.Builder()
                .url(mockServer.url("/"))
                .apply {
                    GlobalOpenObserveTracer.get()
                        .propagate()
                        .inject(parentSpan.context(), this) { _, key, value ->
                            addHeader(key, value)
                        }
                }
                .build()
        ).execute()
        parentSpan.finish()

        // Then
        val requestSent = mockServer.takeRequest()
        assertThat(requestSent.getHeader(OPENOBSERVE_SAMPLING_PRIORITY_HEADER)).isEqualTo("1")
        val leastSignificantTraceId = requestSent.getHeader(OPENOBSERVE_TRACE_ID_HEADER)
        checkNotNull(leastSignificantTraceId)
        val spanId = requestSent.getHeader(OPENOBSERVE_SPAN_ID_HEADER)
        checkNotNull(spanId)
        val ooTags = requestSent.getHeader(OPENOBSERVE_TAGS_HEADER)
            ?.toTags()
        checkNotNull(ooTags)
        assertThat(ooTags).isNotEmpty
        assertThat(ooTags).containsKey(MOST_SIGNIFICANT_TRACE_ID_KEY)
        val mostSignificantTraceId = ooTags.getValue(MOST_SIGNIFICANT_TRACE_ID_KEY)

        val eventsWritten = stubSdkCore.eventsWritten(Feature.TRACING_FEATURE_NAME)
        assertThat(eventsWritten).hasSize(2)

        val localSpanPayload = JsonParser.parseString(eventsWritten[1].eventData).asJsonObject
        SpansPayloadAssert.assertThat(localSpanPayload)
            .hasSpanAtIndexWith(0) {
                hasLeastSignificant64BitsTraceId(
                    leastSignificantTraceId.toLong().toHexString().padStart(16, '0')
                )
                hasMostSignificant64BitsTraceId(mostSignificantTraceId)
                hasParentId("0000000000000000")
                hasAgentPsr(1.0)
                hasSamplingPriority(OpenObserveTracingConstants.PrioritySampling.SAMPLER_KEEP)
                hasGenericMetricValue("_top_level", 1)
            }

        val localSpanId = getLocalSpanId(localSpanPayload)

        val okHttpSpanPayload = JsonParser.parseString(eventsWritten[0].eventData) as JsonObject
        SpansPayloadAssert.assertThat(okHttpSpanPayload)
            .hasEnv(stubSdkCore.getOpenObserveContext().env)
            .hasSpanAtIndexWith(0) {
                hasLeastSignificant64BitsTraceId(
                    leastSignificantTraceId.toLong().toHexString().padStart(16, '0')
                )
                hasMostSignificant64BitsTraceId(mostSignificantTraceId)
                hasSpanId(spanId.toHexPaddedFromDecimalString())
                hasParentId(localSpanId.toHexPaddedFromHexString())
                hasVersion(stubSdkCore.getOpenObserveContext().version)
                hasSource(stubSdkCore.getOpenObserveContext().source)
                hasTracerVersion(stubSdkCore.getOpenObserveContext().sdkVersion)
                hasError(0)
                hasName("okhttp.request")
                hasResource("http://${mockServer.hostName}:${mockServer.port}/")
                hasNoAgentPsr()
                // this one will have sampling priority unlike in case of propagation with tagged Span directly,
                // because there sampling priority is not yet set at the parent during child creation
                hasSamplingPriority(1)
                hasNoGenericMetric("_top_level")
                hasSpanKind("client")
                hasHttpMethod("GET")
                hasHttpUrl("http://${mockServer.hostName}:${mockServer.port}/")
                hasHttpStatusCode(200)
            }
    }

    // endregion

    private fun String.toTags(): Map<String, String> = split(",")
        .associate { it.split("=").let { it[0] to it[1] } }

    private fun String.toHexPaddedFromHexString(): String = with(_TraceInternalProxy.spanIdConverter) {
        toHexStringPadded(fromHex(this@toHexPaddedFromHexString))
    }

    private fun String.toHexPaddedFromDecimalString(): String = with(_TraceInternalProxy.spanIdConverter) {
        toHexStringPadded(this@toHexPaddedFromDecimalString.toLong())
    }

    private fun Request.Builder.parentSpan(span: OpenObserveSpan): Request.Builder {
        tag(OpenObserveSpan::class.java, span)
        return this
    }

    @OptIn(ExperimentalTraceApi::class, ExperimentalRumApi::class)
    private fun buildInstrumentedClient(sampleRate: Float = 100f): OkHttpClient {
        return OkHttpClient.Builder()
            .configureOpenObserveInstrumentation(
                rumInstrumentationConfiguration = null,
                apmInstrumentationConfiguration = ApmNetworkInstrumentationConfiguration(
                    mapOf(mockServer.hostName to setOf(TracingHeaderType.OPENOBSERVE))
                )
                    .setTraceContextInjection(TraceContextInjection.ALL)
                    .setSdkInstanceName(stubSdkCore.name)
                    .setTraceSampleRate(sampleRate)
            )
            .build()
    }

    companion object {
        private const val OPENOBSERVE_TAGS_HEADER = "x-datadog-tags"
        private const val OPENOBSERVE_TRACE_ID_HEADER = "x-datadog-trace-id"
        private const val OPENOBSERVE_SPAN_ID_HEADER = "x-datadog-parent-id"
        private const val OPENOBSERVE_SAMPLING_PRIORITY_HEADER = "x-datadog-sampling-priority"
        private const val MOST_SIGNIFICANT_TRACE_ID_KEY = "_oo.p.tid"
    }
}
