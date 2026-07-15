/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */
package com.openobserve.android.okhttp

import android.content.Context
import com.openobserve.android.OpenObserve
import com.openobserve.android.api.SdkCore
import com.openobserve.android.api.context.AccountInfo
import com.openobserve.android.api.context.OpenObserveContext
import com.openobserve.android.api.context.UserInfo
import com.openobserve.android.api.feature.Feature
import com.openobserve.android.api.feature.SdkFeatureMock
import com.openobserve.android.core.sampling.DeterministicSampler.Companion.MAX_ID
import com.openobserve.android.core.sampling.DeterministicSampler.Companion.SAMPLER_HASHER
import com.openobserve.android.core.stub.StubSDKCore
import com.openobserve.android.okhttp.RumContextPropagationTest.Companion.SAMPLING_THRESHOLD
import com.openobserve.android.okhttp.tests.elmyr.OkHttpConfigurator
import com.openobserve.android.okhttp.tests.utils.unregisterGlobalRumMonitor
import com.openobserve.android.okhttp.trace.TracingInterceptor
import com.openobserve.android.trace.OpenObserveTracing
import com.openobserve.android.trace.GlobalOpenObserveTracer
import com.openobserve.android.trace.Trace
import com.openobserve.android.trace.TraceConfiguration
import com.openobserve.android.trace.TraceContextInjection
import com.openobserve.android.trace.TracingHeaderType
import com.openobserve.android.trace.api.TestIdGenerationStrategy
import com.openobserve.android.trace.api.replace
import com.openobserve.android.trace.api.setTestIdGenerationStrategy
import com.openobserve.android.trace.api.tracer.OpenObserveTracerBuilder
import com.openobserve.android.trace.utils.RumContextTestsUtils.aOpenObserveContextWithRumContext
import com.openobserve.android.trace.utils.RumContextTestsUtils.aRumContext
import com.openobserve.tools.unit.completedFutureMock
import com.openobserve.tools.unit.extensions.TestConfigurationExtension
import com.openobserve.tools.unit.getFieldValue
import com.openobserve.tools.unit.getStaticValue
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.junit5.ForgeConfiguration
import fr.xgouchet.elmyr.junit5.ForgeExtension
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
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
class RumContextPropagationTest {
    private lateinit var mockServer: MockWebServer
    private lateinit var testedClient: OkHttpClient
    private lateinit var stubSdkCore: SdkCore

    @BeforeEach
    fun `set up`() {
        mockServer = MockWebServer().apply {
            enqueue(MockResponse())
            start()
        }
    }

    @AfterEach
    fun `tear down`() {
        unregisterGlobalRumMonitor(stubSdkCore)
        OpenObserve.stopInstance(stubSdkCore.name)
        mockServer.shutdown()
    }

    @Test
    fun `M send rum sessionId in baggage header W call is made`(forge: Forge) {
        // Given
        val rumContext = forge.aRumContext(SAMPLED_IDS.random())
        val accountInfo = forge.getForgery<AccountInfo>()
        val userInfo = forge.getForgery<UserInfo>()
        val ooContext = forge.aOpenObserveContextWithRumContext(rumContext, accountInfo, userInfo)
        stubSdkCore = forge.prepareStubSdkCore(ooContext)
        Trace.enable(TraceConfiguration.Builder().build(), stubSdkCore)
        testedClient = prepareClient(stubSdkCore)
        GlobalOpenObserveTracer.replace(createTracer(stubSdkCore))

        // When
        testedClient.makeNetworkCall()

        // Then
        assertSentRequest {
            assertThat(getHeader(HEADER_BAGGAGE)).isEqualTo(
                "account.id=${accountInfo.id}," +
                    userInfo.id?.let { "user.id=$it," }.orEmpty() +
                    "session.id=${rumContext[RUM_CONTEXT_SESSION_ID]}"
            )
        }
    }

    // region sampleId/tracingId sampling

    @Test
    fun `M set header x-datadog-sampling-priority=1 W call is made { sessionId sampled }`(
        forge: Forge
    ) {
        // Given
        val rumContext = forge.aRumContext(sessionId = SAMPLED_IDS.random())
        val ooContext = forge.aOpenObserveContextWithRumContext(rumContext)
        stubSdkCore = forge.prepareStubSdkCore(ooContext)
        Trace.enable(TraceConfiguration.Builder().build(), stubSdkCore)
        testedClient = prepareClient(stubSdkCore)
        GlobalOpenObserveTracer.replace(createTracer(stubSdkCore))

        // When
        testedClient.makeNetworkCall()

        // Then
        assertSentRequest {
            assertThat(getHeader(HEADER_SAMPLE_PRIORITY)).isEqualTo("1")
        }
    }

    @Test
    fun `M set header x-datadog-sampling-priority=0 W call is made { sessionId not sampled }`(
        forge: Forge
    ) {
        // Given
        val rumContext = forge.aRumContext(sessionId = DROPPED_IDS.random())
        val ooContext = forge.aOpenObserveContextWithRumContext(rumContext)
        stubSdkCore = forge.prepareStubSdkCore(ooContext)
        Trace.enable(TraceConfiguration.Builder().build(), stubSdkCore)
        testedClient = prepareClient(stubSdkCore)
        GlobalOpenObserveTracer.replace(createTracer(stubSdkCore))

        // When
        testedClient.makeNetworkCall()

        // Then
        assertSentRequest {
            assertThat(getHeader(HEADER_SAMPLE_PRIORITY)).isEqualTo("0")
        }
    }

    @Test
    @Suppress("MISSING_DEPENDENCY_SUPERCLASS_WARNING") // it's okay for tests
    fun `M set header x-datadog-sampling-priority=0 W call is made { sessionId = null, traceId not sampled }`(
        forge: Forge
    ) {
        // Given
        val rumContext = forge.aRumContext(sessionId = null)
        val ooContext = forge.aOpenObserveContextWithRumContext(rumContext)
        stubSdkCore = forge.prepareStubSdkCore(ooContext)
        Trace.enable(TraceConfiguration.Builder().build(), stubSdkCore)
        testedClient = prepareClient(stubSdkCore)
        GlobalOpenObserveTracer.replace(createTracer(stubSdkCore).withTraceIdsFrom(DROPPED_IDS))

        // When
        testedClient.makeNetworkCall()

        // Then
        assertSentRequest {
            assertThat(getHeader(HEADER_SAMPLE_PRIORITY)).isEqualTo("0")
        }
    }

    @Test
    @Suppress("MISSING_DEPENDENCY_SUPERCLASS_WARNING") // it's okay for tests
    fun `M set header x-datadog-sampling-priority=1 W call is made { sessionId = null, traceId sampled }`(
        forge: Forge
    ) {
        // Given
        val rumContext = forge.aRumContext(sessionId = null)
        val ooContext = forge.aOpenObserveContextWithRumContext(rumContext)
        stubSdkCore = forge.prepareStubSdkCore(ooContext)
        Trace.enable(TraceConfiguration.Builder().build(), stubSdkCore)
        testedClient = prepareClient(stubSdkCore)
        GlobalOpenObserveTracer.replace(createTracer(stubSdkCore).withTraceIdsFrom(SAMPLED_IDS))

        // When
        testedClient.makeNetworkCall()

        // Then
        assertSentRequest {
            assertThat(getHeader(HEADER_SAMPLE_PRIORITY)).isEqualTo("1")
        }
    }

    // endregion

    // region utilities
    private fun OkHttpClient.makeNetworkCall() {
        newCall(
            Request.Builder()
                .url(mockServer.url("/"))
                .build()
        ).execute()
    }

    private fun prepareClient(sdkCore: SdkCore) = OkHttpClient.Builder()
        .addInterceptor(
            TracingInterceptor.Builder(mapOf(mockServer.hostName to setOf(TracingHeaderType.OPENOBSERVE)))
                .setTraceContextInjection(TraceContextInjection.ALL)
                .setSdkInstanceName(sdkCore.name)
                .setTraceSampleRate(SAMPLE_RATE)
                .build()
        )
        .build()

    // endregion

    companion object {
        /**
         * The sample decision is made in the DeterministicSampler by the following logic:
         * val hash = idConverter(item).SAMPLER_HASHER
         * val threshold = (MAX_ID.toDouble() * sampleRate / SAMPLE_ALL_RATE).toULong()
         * isSampled = hash < threshold
         *
         * Setting the sample rate to a constant value (50% in this test case) allows us to derive the IDs.
         * that will either be sampled or dropped. The [SAMPLING_THRESHOLD] is the derived value.
         * All values below the threshold will be sampled, and all values above the threshold will
         * be dropped.
         *
         * isSampled = SAMPLING_THRESHOLD < MAX_ID / (2 * SAMPLE_HASHER).
         */
        private val SAMPLING_THRESHOLD: Long = (MAX_ID.toDouble() / (2.0 * SAMPLER_HASHER.toDouble())).toLong()
        private const val SAMPLE_RATE = 50f
        private val SAMPLED_IDS = listOf(SAMPLING_THRESHOLD - 1, SAMPLING_THRESHOLD - 2)
        private val DROPPED_IDS = listOf(SAMPLING_THRESHOLD + 1, SAMPLING_THRESHOLD + 2)

        private const val HEADER_BAGGAGE = "baggage"
        private const val HEADER_SAMPLE_PRIORITY = "x-datadog-sampling-priority"

        private const val RUM_CONTEXT_SESSION_ID = "session_id"

        private fun RumContextPropagationTest.assertSentRequest(block: RecordedRequest.() -> Unit) {
            block(mockServer.takeRequest())
        }

        private fun Forge.prepareStubSdkCore(ooContext: OpenObserveContext): StubSDKCore {
            val sdkCoreStub = StubSDKCore(this, ooContext = ooContext)

            OpenObserve::class.java
                .getStaticValue<OpenObserve, Any>("registry")
                .getFieldValue<MutableMap<String, SdkCore>, Any>("instances")
                .also { instances -> instances += sdkCoreStub.name to sdkCoreStub }

            sdkCoreStub.stubFeatureScope(
                StubRumFeature,
                SdkFeatureMock.create(completedFutureMock(ooContext))
            )

            return sdkCoreStub
        }

        private fun createTracer(sdkCore: SdkCore) = OpenObserveTracing.newTracerBuilder(sdkCore)
            .withTracingHeadersTypes(setOf(TracingHeaderType.OPENOBSERVE))
            // this is on purpose, we want to make sure that it is not taken into account
            .withSampleRate(100.0)

        @Suppress("MISSING_DEPENDENCY_SUPERCLASS_WARNING") // it's okay for testing
        private fun OpenObserveTracerBuilder.withTraceIdsFrom(traceIds: List<Long>): OpenObserveTracerBuilder =
            setTestIdGenerationStrategy(TestIdGenerationStrategy(traceIds = traceIds))
    }
}

private object StubRumFeature : Feature {
    override val name: String = Feature.RUM_FEATURE_NAME
    override fun onStop() = Unit
    override fun onInitialize(appContext: Context) = Unit
}
