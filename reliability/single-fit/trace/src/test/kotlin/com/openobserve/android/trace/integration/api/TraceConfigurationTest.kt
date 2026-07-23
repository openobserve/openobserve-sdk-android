/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.trace.integration.api

import com.openobserve.android.api.feature.Feature
import com.openobserve.android.api.feature.StorageBackedFeature
import com.openobserve.android.api.net.RequestExecutionContext
import com.openobserve.android.api.storage.RawBatchEvent
import com.openobserve.android.core.stub.StubSDKCore
import com.openobserve.android.tests.ktx.getInt
import com.openobserve.android.tests.ktx.getLong
import com.openobserve.android.tests.ktx.getString
import com.openobserve.android.trace.OpenObserveTracing
import com.openobserve.android.trace.GlobalOpenObserveTracer
import com.openobserve.android.trace.Trace
import com.openobserve.android.trace.TraceConfiguration
import com.openobserve.android.trace.event.SpanEventMapper
import com.openobserve.android.trace.integration.tests.elmyr.TraceIntegrationForgeConfigurator
import com.openobserve.android.trace.model.SpanEvent
import com.openobserve.tools.unit.extensions.TestConfigurationExtension
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.annotation.Forgery
import fr.xgouchet.elmyr.annotation.StringForgery
import fr.xgouchet.elmyr.junit5.ForgeConfiguration
import fr.xgouchet.elmyr.junit5.ForgeExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.quality.Strictness
import java.util.concurrent.TimeUnit
import kotlin.system.measureNanoTime

@Extensions(
    ExtendWith(MockitoExtension::class),
    ExtendWith(ForgeExtension::class),
    ExtendWith(TestConfigurationExtension::class)
)
@ForgeConfiguration(TraceIntegrationForgeConfigurator::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TraceConfigurationTest {

    private lateinit var stubSdkCore: StubSDKCore

    @Forgery
    private lateinit var fakeRequestExecutionContext: RequestExecutionContext

    @BeforeEach
    fun `set up`(forge: Forge) {
        stubSdkCore = StubSDKCore(forge)

        val fakeTraceConfiguration = TraceConfiguration.Builder().build()
        Trace.enable(fakeTraceConfiguration, stubSdkCore)
    }

    @AfterEach
    fun `tear down`() {
        GlobalOpenObserveTracer.clear()
    }

    @RepeatedTest(16)
    fun `M create request to core site W RequestFactory#create()`(
        @Forgery fakeBatch: List<RawBatchEvent>,
        @StringForgery fakeMetadata: String
    ) {
        // Given
        val expectedSite = stubSdkCore.getOpenObserveContext().site
        val expectedClientToken = stubSdkCore.getOpenObserveContext().clientToken
        val expectedSource = stubSdkCore.getOpenObserveContext().source
        val expectedSdkVersion = stubSdkCore.getOpenObserveContext().sdkVersion

        // When
        val traceFeature = stubSdkCore.getFeature(Feature.TRACING_FEATURE_NAME)?.unwrap<StorageBackedFeature>()
        val requestFactory = traceFeature?.requestFactory
        val request = requestFactory?.create(
            stubSdkCore.getOpenObserveContext(),
            fakeRequestExecutionContext,
            fakeBatch,
            fakeMetadata.toByteArray()
        )

        // Then
        checkNotNull(request)
        assertThat(request.url).isEqualTo("${expectedSite.intakeEndpoint}/api/v2/spans")
        assertThat(request.headers).containsEntry("O2-API-KEY", expectedClientToken)
        assertThat(request.headers).containsEntry("OO-EVP-ORIGIN", expectedSource)
        assertThat(request.headers).containsEntry("OO-EVP-ORIGIN-VERSION", expectedSdkVersion)
        assertThat(request.contentType).isEqualTo("text/plain;charset=UTF-8")
    }

    @RepeatedTest(16)
    fun `M create request to custom endpoint W useCustomEndpoint() + RequestFactory#create`(
        @StringForgery fakeEndpoint: String,
        @Forgery fakeBatch: List<RawBatchEvent>,
        @StringForgery fakeMetadata: String
    ) {
        // Given
        val expectedClientToken = stubSdkCore.getOpenObserveContext().clientToken
        val expectedSource = stubSdkCore.getOpenObserveContext().source
        val expectedSdkVersion = stubSdkCore.getOpenObserveContext().sdkVersion
        val fakeTraceConfiguration = TraceConfiguration.Builder()
            .useCustomEndpoint(fakeEndpoint)
            .build()
        Trace.enable(fakeTraceConfiguration, stubSdkCore)

        // When
        val traceFeature = stubSdkCore.getFeature(Feature.TRACING_FEATURE_NAME)?.unwrap<StorageBackedFeature>()
        val requestFactory = traceFeature?.requestFactory
        val request = requestFactory?.create(
            stubSdkCore.getOpenObserveContext(),
            fakeRequestExecutionContext,
            fakeBatch,
            fakeMetadata.toByteArray()
        )

        // Then
        checkNotNull(request)
        assertThat(request.url).isEqualTo(fakeEndpoint)
        assertThat(request.headers).containsEntry("O2-API-KEY", expectedClientToken)
        assertThat(request.headers).containsEntry("OO-EVP-ORIGIN", expectedSource)
        assertThat(request.headers).containsEntry("OO-EVP-ORIGIN-VERSION", expectedSdkVersion)
        assertThat(request.contentType).isEqualTo("text/plain;charset=UTF-8")
    }

    @RepeatedTest(16)
    fun `M send span without network info W setNetworkInfoEnabled(false) + buildSpan() + start() + finish()`(
        @StringForgery fakeOperation: String
    ) {
        // Given
        val fakeTraceConfiguration = TraceConfiguration.Builder()
            .setNetworkInfoEnabled(false)
            .build()
        Trace.enable(fakeTraceConfiguration, stubSdkCore)
        val testedTracer = OpenObserveTracing.newTracerBuilder(stubSdkCore).build()

        // When
        var leastSignificantTraceId: String
        var mostSignificantTraceId: String
        var spanId: String
        val fullDuration = measureNanoTime {
            val span = testedTracer.buildSpan(fakeOperation).start()
            leastSignificantTraceId = span.leastSignificant64BitsTraceId()
            mostSignificantTraceId = span.mostSignificant64BitsTraceId()
            spanId = span.spanIdAsHexString()
            Thread.sleep(OP_DURATION_MS)
            span.finish()
        }

        // Then
        val eventsWritten = stubSdkCore.eventsWritten(Feature.TRACING_FEATURE_NAME)
        assertThat(eventsWritten).hasSize(1)
        val event0 = JsonParser.parseString(eventsWritten[0].eventData) as JsonObject
        assertThat(event0.getString("env")).isEqualTo(stubSdkCore.getOpenObserveContext().env)
        assertThat(event0.getString("spans[0].trace_id")).isEqualTo(leastSignificantTraceId)
        assertThat(event0.getString("spans[0].meta._oo.p.id")).isEqualTo(mostSignificantTraceId)
        assertThat(event0.getString("spans[0].span_id")).isEqualTo(spanId)
        assertThat(event0.getString("spans[0].service")).isEqualTo(stubSdkCore.getOpenObserveContext().service)
        assertThat(event0.getString("spans[0].meta.version")).isEqualTo(stubSdkCore.getOpenObserveContext().version)
        assertThat(event0.getString("spans[0].meta._oo.source")).isEqualTo(stubSdkCore.getOpenObserveContext().source)
        assertThat(event0.getString("spans[0].meta.tracer.version"))
            .isEqualTo(stubSdkCore.getOpenObserveContext().sdkVersion)
        assertThat(event0.getInt("spans[0].error")).isEqualTo(0)
        assertThat(event0.getString("spans[0].name")).isEqualTo(fakeOperation)
        assertThat(event0.getString("spans[0].resource")).isEqualTo(fakeOperation)
        assertThat(event0.getLong("spans[0].duration")).isBetween(OP_DURATION_NS, fullDuration)
        assertThat(event0.getString("spans[0].meta.network.client.connectivity")).isNull()
        assertThat(event0.getString("spans[0].meta.network.client.sim_carrier.name")).isNull()
        assertThat(event0.getString("spans[0].meta.network.client.sim_carrier.id")).isNull()
    }

    @RepeatedTest(16)
    fun `M send span with network info W setNetworkInfoEnabled(true) + buildSpan() + start() + finish()`(
        @StringForgery fakeOperation: String
    ) {
        // Given
        val fakeTraceConfiguration = TraceConfiguration.Builder()
            .setNetworkInfoEnabled(true)
            .build()
        Trace.enable(fakeTraceConfiguration, stubSdkCore)
        val testedTracer = OpenObserveTracing.newTracerBuilder(stubSdkCore).build()

        // When
        var leastSignificantTraceId: String
        var mostSignificantTraceId: String
        var spanId: String
        val fullDuration = measureNanoTime {
            val span = testedTracer.buildSpan(fakeOperation).start()
            leastSignificantTraceId = span.leastSignificant64BitsTraceId()
            mostSignificantTraceId = span.mostSignificant64BitsTraceId()
            spanId = span.spanIdAsHexString()
            Thread.sleep(OP_DURATION_MS)
            span.finish()
        }

        // Then
        val eventsWritten = stubSdkCore.eventsWritten(Feature.TRACING_FEATURE_NAME)
        assertThat(eventsWritten).hasSize(1)
        val event0 = JsonParser.parseString(eventsWritten[0].eventData) as JsonObject
        assertThat(event0.getString("env")).isEqualTo(stubSdkCore.getOpenObserveContext().env)
        assertThat(event0.getString("spans[0].trace_id")).isEqualTo(leastSignificantTraceId)
        assertThat(event0.getString("spans[0].meta._oo.p.id")).isEqualTo(mostSignificantTraceId)
        assertThat(event0.getString("spans[0].span_id")).isEqualTo(spanId)
        assertThat(event0.getString("spans[0].service")).isEqualTo(stubSdkCore.getOpenObserveContext().service)
        assertThat(event0.getString("spans[0].meta.version")).isEqualTo(stubSdkCore.getOpenObserveContext().version)
        assertThat(event0.getString("spans[0].meta._oo.source")).isEqualTo(stubSdkCore.getOpenObserveContext().source)
        assertThat(event0.getString("spans[0].meta.tracer.version"))
            .isEqualTo(stubSdkCore.getOpenObserveContext().sdkVersion)
        assertThat(event0.getInt("spans[0].error")).isEqualTo(0)
        assertThat(event0.getString("spans[0].name")).isEqualTo(fakeOperation)
        assertThat(event0.getString("spans[0].resource")).isEqualTo(fakeOperation)
        assertThat(event0.getLong("spans[0].duration")).isBetween(OP_DURATION_NS, fullDuration)
        assertThat(event0.getString("spans[0].meta.network.client.connectivity"))
            .isEqualTo(stubSdkCore.getOpenObserveContext().networkInfo.connectivity.name)
        assertThat(event0.getString("spans[0].meta.network.client.sim_carrier.name"))
            .isEqualTo(stubSdkCore.getOpenObserveContext().networkInfo.carrierName)
        assertThat(event0.getLong("spans[0].meta.network.client.sim_carrier.id"))
            .isEqualTo(stubSdkCore.getOpenObserveContext().networkInfo.carrierId)
    }

    @RepeatedTest(16)
    fun `M send mapped span W setEventMapper() + buildSpan() + start() + finish()`(
        @StringForgery fakeOperation: String,
        @StringForgery fakeMappedOperation: String,
        @StringForgery fakeMappedResource: String
    ) {
        // Given
        val stubMapper = object : SpanEventMapper {
            override fun map(event: SpanEvent): SpanEvent {
                event.name = fakeMappedOperation
                event.resource = fakeMappedResource
                return event
            }
        }
        val fakeTraceConfiguration = TraceConfiguration.Builder()
            .setEventMapper(stubMapper)
            .build()
        Trace.enable(fakeTraceConfiguration, stubSdkCore)
        val testedTracer = OpenObserveTracing.newTracerBuilder(stubSdkCore).build()

        // When
        val fullDuration = measureNanoTime {
            val span = testedTracer.buildSpan(fakeOperation).start()
            Thread.sleep(OP_DURATION_MS)
            span.finish()
        }

        // Then
        val eventsWritten = stubSdkCore.eventsWritten(Feature.TRACING_FEATURE_NAME)
        assertThat(eventsWritten).hasSize(1)
        val event0 = JsonParser.parseString(eventsWritten[0].eventData) as JsonObject
        assertThat(event0.getString("env")).isEqualTo(stubSdkCore.getOpenObserveContext().env)
        assertThat(event0.getString("spans[0].service")).isEqualTo(stubSdkCore.getOpenObserveContext().service)
        assertThat(event0.getString("spans[0].meta.version")).isEqualTo(stubSdkCore.getOpenObserveContext().version)
        assertThat(event0.getString("spans[0].meta._oo.source")).isEqualTo(stubSdkCore.getOpenObserveContext().source)
        assertThat(event0.getString("spans[0].meta.tracer.version"))
            .isEqualTo(stubSdkCore.getOpenObserveContext().sdkVersion)
        assertThat(event0.getInt("spans[0].error")).isEqualTo(0)
        assertThat(event0.getString("spans[0].name")).isEqualTo(fakeMappedOperation)
        assertThat(event0.getString("spans[0].resource")).isEqualTo(fakeMappedResource)
        assertThat(event0.getLong("spans[0].duration")).isBetween(OP_DURATION_NS, fullDuration)
    }

    companion object {
        const val OP_DURATION_MS = 10L
        val OP_DURATION_NS = TimeUnit.MILLISECONDS.toNanos(OP_DURATION_MS)
    }
}
