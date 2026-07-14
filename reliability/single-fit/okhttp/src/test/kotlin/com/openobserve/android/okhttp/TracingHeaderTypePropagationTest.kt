/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.okhttp

import com.openobserve.android.OpenObserve
import com.openobserve.android.api.SdkCore
import com.openobserve.android.core.stub.StubSDKCore
import com.openobserve.android.okhttp.tests.elmyr.OkHttpConfigurator
import com.openobserve.android.okhttp.trace.TracingInterceptor
import com.openobserve.android.trace.OpenObserveTracing
import com.openobserve.android.trace.GlobalOpenObserveTracer
import com.openobserve.android.trace.Trace
import com.openobserve.android.trace.TraceConfiguration
import com.openobserve.android.trace.TraceContextInjection
import com.openobserve.android.trace.TracingHeaderType
import com.openobserve.tools.unit.getFieldValue
import com.openobserve.tools.unit.getStaticValue
import fr.xgouchet.elmyr.Forge
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
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.quality.Strictness

@Extensions(ExtendWith(ForgeExtension::class))
@ForgeConfiguration(OkHttpConfigurator::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TracingHeaderTypePropagationTest {

    private lateinit var stubSdkCore: StubSDKCore
    private lateinit var mockServer: MockWebServer

    @BeforeEach
    fun `set up`(forge: Forge) {
        stubSdkCore = StubSDKCore(forge)
        val registry: Any = OpenObserve::class.java.getStaticValue("registry")
        val instances: MutableMap<String, SdkCore> = registry.getFieldValue("instances")
        instances += stubSdkCore.name to stubSdkCore

        mockServer = MockWebServer()

        Trace.enable(TraceConfiguration.Builder().build(), stubSdkCore)
    }

    @AfterEach
    fun `tear down`() {
        GlobalOpenObserveTracer.clear()
        OpenObserve.stopInstance(stubSdkCore.name)
        mockServer.shutdown()
    }

    @Test
    fun `M inject B3 single header W call is made { B3 configured }`() {
        verifyHeadersInjected(
            TracingHeaderType.B3,
            HEADER_B3
        )
    }

    @Test
    fun `M inject B3Multi headers W call is made { B3MULTI configured }`() {
        verifyHeadersInjected(
            TracingHeaderType.B3MULTI,
            HEADER_B3_TRACE_ID,
            HEADER_B3_SPAN_ID,
            HEADER_B3_SAMPLED
        )
    }

    @Test
    fun `M inject OpenObserve headers W call is made { OPENOBSERVE configured }`() {
        verifyHeadersInjected(
            TracingHeaderType.OPENOBSERVE,
            HEADER_DD_TRACE_ID,
            HEADER_DD_PARENT_ID,
            HEADER_DD_SAMPLING_PRIORITY
        )
    }

    @Test
    fun `M inject TraceContext headers W call is made { TRACECONTEXT configured }`() {
        verifyHeadersInjected(
            TracingHeaderType.TRACECONTEXT,
            HEADER_TRACEPARENT,
            HEADER_TRACESTATE
        )
    }

    // region utilities

    private fun verifyHeadersInjected(
        headerType: TracingHeaderType,
        vararg expectedHeaders: String
    ) {
        // Given
        mockServer.enqueue(MockResponse())
        mockServer.start()
        val tracer = OpenObserveTracing.newTracerBuilder(stubSdkCore)
            .withTracingHeadersTypes(setOf(headerType))
            .withSampleRate(100.0)
            .build()
        GlobalOpenObserveTracer.registerIfAbsent(tracer)
        val client = OkHttpClient.Builder()
            .addInterceptor(
                TracingInterceptor.Builder(mapOf(mockServer.hostName to setOf(headerType)))
                    .setTraceContextInjection(TraceContextInjection.ALL)
                    .setSdkInstanceName(stubSdkCore.name)
                    .build()
            )
            .build()

        // When
        client.newCall(Request.Builder().url(mockServer.url("/")).build()).execute()

        // Then
        val recordedRequest = mockServer.takeRequest()
        expectedHeaders.forEach { header ->
            assertThat(recordedRequest.getHeader(header)).isNotEmpty()
        }
    }

    // endregion

    companion object {
        // B3 Single header
        private const val HEADER_B3 = "b3"

        // B3 Multi headers
        private const val HEADER_B3_TRACE_ID = "X-B3-TraceId"
        private const val HEADER_B3_SPAN_ID = "X-B3-SpanId"
        private const val HEADER_B3_SAMPLED = "X-B3-Sampled"

        // OpenObserve headers
        private const val HEADER_DD_TRACE_ID = "x-datadog-trace-id"
        private const val HEADER_DD_PARENT_ID = "x-datadog-parent-id"
        private const val HEADER_DD_SAMPLING_PRIORITY = "x-datadog-sampling-priority"

        // W3C TraceContext headers
        private const val HEADER_TRACEPARENT = "traceparent"
        private const val HEADER_TRACESTATE = "tracestate"
    }
}
