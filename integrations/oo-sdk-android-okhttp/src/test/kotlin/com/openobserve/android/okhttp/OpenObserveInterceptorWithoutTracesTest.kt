/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.okhttp

import com.openobserve.android.api.InternalLogger
import com.openobserve.android.api.feature.Feature
import com.openobserve.android.core.internal.net.DefaultFirstPartyHostHeaderTypeResolver
import com.openobserve.android.core.sampling.Sampler
import com.openobserve.android.okhttp.trace.TracedRequestListener
import com.openobserve.android.okhttp.trace.TracingInterceptor
import com.openobserve.android.okhttp.trace.TracingInterceptorTest
import com.openobserve.android.okhttp.trace.aOpenObserveTraceId
import com.openobserve.android.okhttp.trace.newAgentPropagationMock
import com.openobserve.android.okhttp.trace.newSpanBuilderMock
import com.openobserve.android.okhttp.trace.newSpanContextMock
import com.openobserve.android.okhttp.trace.newSpanMock
import com.openobserve.android.okhttp.trace.newTracerMock
import com.openobserve.android.okhttp.utils.config.GlobalRumMonitorTestConfiguration
import com.openobserve.android.rum.RumErrorSource
import com.openobserve.android.rum.RumResourceAttributesProvider
import com.openobserve.android.rum.RumResourceKind
import com.openobserve.android.rum.RumResourceMethod
import com.openobserve.android.rum.resource.ResourceId
import com.openobserve.android.tests.config.OpenObserveSingletonTestConfiguration
import com.openobserve.android.tests.elmyr.anOkHttpResponse
import com.openobserve.android.trace.TraceContextInjection
import com.openobserve.android.trace.api.propagation.OpenObservePropagation
import com.openobserve.android.trace.api.span.OpenObserveSpan
import com.openobserve.android.trace.api.span.OpenObserveSpanBuilder
import com.openobserve.android.trace.api.span.OpenObserveSpanContext
import com.openobserve.android.trace.api.trace.OpenObserveTraceId
import com.openobserve.android.trace.api.tracer.OpenObserveTracer
import com.openobserve.android.utils.verifyLog
import com.openobserve.tools.unit.annotations.TestConfigurationsProvider
import com.openobserve.tools.unit.extensions.TestConfigurationExtension
import com.openobserve.tools.unit.extensions.config.TestConfiguration
import com.openobserve.tools.unit.forge.BaseConfigurator
import com.openobserve.tools.unit.forge.exhaustiveAttributes
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.annotation.BoolForgery
import fr.xgouchet.elmyr.annotation.Forgery
import fr.xgouchet.elmyr.annotation.IntForgery
import fr.xgouchet.elmyr.annotation.LongForgery
import fr.xgouchet.elmyr.annotation.StringForgery
import fr.xgouchet.elmyr.annotation.StringForgeryType
import fr.xgouchet.elmyr.junit5.ForgeConfiguration
import fr.xgouchet.elmyr.junit5.ForgeExtension
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.eq
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness
import java.util.Locale

@Extensions(
    ExtendWith(MockitoExtension::class),
    ExtendWith(ForgeExtension::class),
    ExtendWith(TestConfigurationExtension::class)
)
@MockitoSettings(strictness = Strictness.LENIENT)
@ForgeConfiguration(value = BaseConfigurator::class)
internal class OpenObserveInterceptorWithoutTracesTest {

    lateinit var testedInterceptor: TracingInterceptor

    // region Mocks

    private lateinit var mockLocalTracer: OpenObserveTracer

    private lateinit var mockSpanBuilder: OpenObserveSpanBuilder

    private lateinit var mockSpanContext: OpenObserveSpanContext

    private lateinit var mockPropagation: OpenObservePropagation

    private lateinit var fakeSpan: OpenObserveSpan

    @Mock
    lateinit var mockChain: Interceptor.Chain

    @Mock
    lateinit var mockRequestListener: TracedRequestListener

    @Mock
    lateinit var mockRumAttributesProvider: RumResourceAttributesProvider

    @Mock
    lateinit var mockResolver: DefaultFirstPartyHostHeaderTypeResolver

    @Mock
    lateinit var mockTraceSampler: Sampler<OpenObserveSpan>

    @Mock
    lateinit var mockInternalLogger: InternalLogger

    // endregion

    // region Fakes

    lateinit var fakeMethod: RumResourceMethod
    var fakeBody: String? = null
    var fakeMediaType: MediaType? = null

    @StringForgery(type = StringForgeryType.ASCII)
    lateinit var fakeResponseBody: String

    lateinit var fakeUrl: String

    lateinit var fakeRequest: Request
    lateinit var fakeResponse: Response

    lateinit var fakeResourceAttributes: Map<String, Any?>

    @LongForgery
    var fakeSpanId: Long = 0L

    @StringForgery(regex = "[a-f][0-9]{31}")
    lateinit var fakeTraceIdString: String

    lateinit var fakeTraceId: OpenObserveTraceId

    @BoolForgery
    var fakeRedacted404Resources: Boolean = true

    lateinit var forge: Forge

    // endregion

    @BeforeEach
    fun `set up`(forge: Forge) {
        this.forge = forge
        fakeTraceId = forge.aOpenObserveTraceId(fakeTraceIdString)
        mockSpanContext = forge.newSpanContextMock(fakeTraceId, fakeSpanId)
        fakeSpan = forge.newSpanMock(mockSpanContext)
        mockSpanBuilder = forge.newSpanBuilderMock(fakeSpan, mockSpanContext)
        mockPropagation = newAgentPropagationMock()
        mockLocalTracer = forge.newTracerMock(mockSpanBuilder, mockPropagation)

        whenever(fakeSpan.samplingPriority) doReturn null
        whenever(mockTraceSampler.sample(any())) doReturn true
        whenever(rumMonitor.mockSdkCore.firstPartyHostResolver) doReturn mockResolver

        val mediaType = forge.anElementFrom("application", "image", "text", "model") +
            "/" + forge.anAlphabeticalString()
        fakeMediaType = mediaType.toMediaTypeOrNull()
        fakeRequest = forgeRequest()
        testedInterceptor = OpenObserveInterceptor(
            sdkInstanceName = null,
            tracedHosts = emptyMap(),
            tracedRequestListener = mockRequestListener,
            rumResourceAttributesProvider = mockRumAttributesProvider,
            traceSampler = mockTraceSampler,
            redacted404ResourceName = fakeRedacted404Resources,
            traceContextInjection = TraceContextInjection.ALL,
            localTracerFactory = { _, _ -> mockLocalTracer },
            globalTracerProvider = { null }
        )
        whenever(rumMonitor.mockSdkCore.getFeature(Feature.TRACING_FEATURE_NAME)) doReturn mock()
        whenever(rumMonitor.mockSdkCore.getFeature(Feature.RUM_FEATURE_NAME)) doReturn mock()
        whenever(rumMonitor.mockSdkCore.internalLogger) doReturn mockInternalLogger

        fakeResourceAttributes = forge.exhaustiveAttributes()

        @Suppress("DEPRECATION")
        whenever(
            mockRumAttributesProvider.onProvideAttributes(
                any<Request>(),
                anyOrNull<Response>(),
                anyOrNull<Throwable>()
            )
        ) doReturn fakeResourceAttributes
    }

    @Test
    fun `M start and stop RUM Resource W intercept() for successful request`(
        @IntForgery(min = 200, max = 300) statusCode: Int
    ) {
        // Given
        stubChain(mockChain, statusCode)
        val expectedStartAttrs = emptyMap<String, Any?>()
        val expectedStopAttrs = fakeResourceAttributes
        val mimeType = fakeMediaType?.type
        val kind = when {
            mimeType != null -> RumResourceKind.fromMimeType(mimeType)
            else -> RumResourceKind.NATIVE
        }

        // When
        testedInterceptor.intercept(mockChain)

        // Then
        inOrder(rumMonitor.mockInstance) {
            argumentCaptor<ResourceId> {
                verify(rumMonitor.mockInstance).startResource(
                    capture(),
                    eq(fakeMethod),
                    eq(fakeUrl),
                    eq(expectedStartAttrs)
                )
                verify(rumMonitor.mockInstance).stopResource(
                    capture(),
                    eq(statusCode),
                    eq(fakeResponseBody.toByteArray().size.toLong()),
                    eq(kind),
                    eq(expectedStopAttrs)
                )
                assertThat(firstValue).isEqualTo(secondValue)
            }
        }
    }

    @Test
    fun `M start and stop RUM Resource W intercept() for successful request { unknown method }`(
        @IntForgery(min = 200, max = 300) statusCode: Int,
        @StringForgery fakeMethod: String
    ) {
        // Given
        fakeRequest = forgeRequest {
            it.method(fakeMethod, null)
        }
        stubChain(mockChain, statusCode)
        val expectedStartAttrs = emptyMap<String, Any?>()
        val expectedStopAttrs = fakeResourceAttributes
        val mimeType = fakeMediaType?.type
        val kind = when {
            mimeType != null -> RumResourceKind.fromMimeType(mimeType)
            else -> RumResourceKind.NATIVE
        }

        // When
        testedInterceptor.intercept(mockChain)

        // Then
        inOrder(rumMonitor.mockInstance) {
            argumentCaptor<ResourceId> {
                verify(rumMonitor.mockInstance).startResource(
                    capture(),
                    eq(RumResourceMethod.GET),
                    eq(fakeUrl),
                    eq(expectedStartAttrs)
                )
                verify(rumMonitor.mockInstance).stopResource(
                    capture(),
                    eq(statusCode),
                    eq(fakeResponseBody.toByteArray().size.toLong()),
                    eq(kind),
                    eq(expectedStopAttrs)
                )
                assertThat(firstValue).isEqualTo(secondValue)
            }
        }

        mockInternalLogger.verifyLog(
            InternalLogger.Level.WARN,
            targets = listOf(InternalLogger.Target.USER, InternalLogger.Target.TELEMETRY),
            OpenObserveInterceptor.UNSUPPORTED_HTTP_METHOD.format(Locale.US, fakeMethod)
        )
    }

    @Test
    fun `M start and stop RUM Resource W intercept() for failing request`(
        @IntForgery(min = 400, max = 500) statusCode: Int
    ) {
        // Given
        stubChain(mockChain, statusCode)
        val expectedStartAttrs = emptyMap<String, Any?>()
        val expectedStopAttrs = fakeResourceAttributes
        val mimeType = fakeMediaType?.type
        val kind = when {
            mimeType != null -> RumResourceKind.fromMimeType(mimeType)
            else -> RumResourceKind.NATIVE
        }

        // When
        testedInterceptor.intercept(mockChain)

        // Then
        inOrder(rumMonitor.mockInstance) {
            argumentCaptor<ResourceId> {
                verify(rumMonitor.mockInstance).startResource(
                    capture(),
                    eq(fakeMethod),
                    eq(fakeUrl),
                    eq(expectedStartAttrs)
                )
                verify(rumMonitor.mockInstance).stopResource(
                    capture(),
                    eq(statusCode),
                    eq(fakeResponseBody.toByteArray().size.toLong()),
                    eq(kind),
                    eq(expectedStopAttrs)
                )
                assertThat(firstValue).isEqualTo(secondValue)
            }
        }
    }

    @Test
    fun `M starts and stop RUM Resource W intercept() for throwing request`(
        @Forgery throwable: Throwable
    ) {
        // Given
        val expectedStartAttrs = emptyMap<String, Any?>()
        val expectedStopAttrs = fakeResourceAttributes
        whenever(mockChain.request()) doReturn fakeRequest
        whenever(mockChain.proceed(any())) doThrow throwable

        // When
        assertThrows<Throwable>(throwable.message.orEmpty()) {
            testedInterceptor.intercept(mockChain)
        }

        // Then
        inOrder(rumMonitor.mockInstance) {
            argumentCaptor<ResourceId> {
                verify(rumMonitor.mockInstance).startResource(
                    capture(),
                    eq(fakeMethod),
                    eq(fakeUrl),
                    eq(expectedStartAttrs)
                )
                verify(rumMonitor.mockInstance).stopResourceWithError(
                    capture(),
                    eq(null),
                    eq("OkHttp request error $fakeMethod ${fakeUrl.lowercase(Locale.US)}"),
                    eq(RumErrorSource.NETWORK),
                    eq(throwable),
                    eq(expectedStopAttrs)
                )
                assertThat(firstValue).isEqualTo(secondValue)
            }
        }
    }

    @Test
    fun `M create and drop a Span with info W intercept() for successful request`(
        @IntForgery(min = 200, max = 300) statusCode: Int
    ) {
        whenever(mockResolver.isFirstPartyUrl(fakeUrl.toHttpUrl())).thenReturn(true)
        stubChain(mockChain, statusCode)

        val response = testedInterceptor.intercept(mockChain)

        verify(mockSpanBuilder).withOrigin(OpenObserveInterceptor.ORIGIN_RUM)
        verify(fakeSpan).drop()
        assertThat(response).isSameAs(fakeResponse)
    }

    @Test
    fun `M create and drop a span with info W intercept() for failing request {4xx}`(
        @IntForgery(min = 400, max = 500) statusCode: Int
    ) {
        whenever(mockResolver.isFirstPartyUrl(fakeUrl.toHttpUrl())).thenReturn(true)
        stubChain(mockChain, statusCode)

        val response = testedInterceptor.intercept(mockChain)

        verify(mockSpanBuilder).withOrigin(OpenObserveInterceptor.ORIGIN_RUM)
        verify(fakeSpan).resourceName = fakeUrl.lowercase(Locale.US)
        verify(fakeSpan).isError = true
        verify(fakeSpan).drop()
        assertThat(response).isSameAs(fakeResponse)
    }

    // region Internal

    private fun stubChain(chain: Interceptor.Chain, statusCode: Int) {
        fakeResponse = forge.anOkHttpResponse(fakeRequest, statusCode) {
            header(TracingInterceptor.HEADER_CT, fakeMediaType?.type.orEmpty())
            body(fakeResponseBody.toResponseBody(fakeMediaType))
        }

        whenever(chain.request()) doReturn fakeRequest
        whenever(chain.proceed(any())) doReturn fakeResponse
    }

    private fun forgeRequest(configure: (Request.Builder) -> Unit = {}): Request {
        val protocol = forge.anElementFrom("http", "https")
        // RUMM-2900 host is by definition case-insensitive,
        // and OkHttp lowercases it when building the request
        val host = forge.aStringMatching(TracingInterceptorTest.HOSTNAME_PATTERN).lowercase(Locale.US)
        val path = forge.anAlphaNumericalString()
        fakeUrl = "$protocol://$host/$path"
        val builder = Request.Builder().url(fakeUrl)
        if (forge.aBool()) {
            fakeMethod = forge.anElementFrom(
                RumResourceMethod.POST,
                RumResourceMethod.PATCH,
                RumResourceMethod.PUT
            )
            fakeBody = forge.anAlphabeticalString()
            builder.method(fakeMethod.name, fakeBody!!.toByteArray().toRequestBody(null))
        } else {
            fakeMethod = forge.anElementFrom(
                RumResourceMethod.GET,
                RumResourceMethod.HEAD,
                RumResourceMethod.DELETE,
                RumResourceMethod.CONNECT,
                RumResourceMethod.TRACE,
                RumResourceMethod.OPTIONS
            )
            fakeBody = null
            builder.method(fakeMethod.name, null)
        }

        configure(builder)

        return builder.build()
    }

    // endregion

    companion object {
        val ooCore = OpenObserveSingletonTestConfiguration()
        val rumMonitor = GlobalRumMonitorTestConfiguration(ooCore)

        @TestConfigurationsProvider
        @JvmStatic
        fun getTestConfigurations(): List<TestConfiguration> {
            return listOf(ooCore, rumMonitor)
        }
    }
}
