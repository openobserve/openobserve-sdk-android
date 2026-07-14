/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.okhttp

import com.openobserve.android.api.InternalLogger
import com.openobserve.android.api.SdkCore
import com.openobserve.android.internal.telemetry.InternalTelemetryEvent
import com.openobserve.android.okhttp.internal.utils.forge.OkHttpConfigurator
import com.openobserve.android.okhttp.trace.TracingInterceptor
import com.openobserve.android.okhttp.trace.TracingInterceptorTest
import com.openobserve.android.rum.RumResourceAttributesProvider
import com.openobserve.android.trace.TraceContextInjection
import com.openobserve.android.trace.TracingHeaderType
import com.openobserve.android.trace.api.tracer.OpenObserveTracer
import com.openobserve.android.utils.verifyLog
import com.openobserve.tools.unit.extensions.TestConfigurationExtension
import fr.xgouchet.elmyr.annotation.Forgery
import fr.xgouchet.elmyr.annotation.IntForgery
import fr.xgouchet.elmyr.junit5.ForgeConfiguration
import fr.xgouchet.elmyr.junit5.ForgeExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness
import java.util.Locale

@Extensions(
    ExtendWith(MockitoExtension::class),
    ExtendWith(ForgeExtension::class),
    ExtendWith(TestConfigurationExtension::class)
)
@MockitoSettings(strictness = Strictness.LENIENT)
@ForgeConfiguration(OkHttpConfigurator::class)
internal class OpenObserveInterceptorWithoutRumTest : TracingInterceptorTest() {

    @Mock
    lateinit var mockRumAttributesProvider: RumResourceAttributesProvider

    override fun instantiateTestedInterceptor(
        tracedHosts: Map<String, Set<TracingHeaderType>>,
        globalTracerProvider: () -> OpenObserveTracer?,
        localTracerFactory: (SdkCore, Set<TracingHeaderType>) -> OpenObserveTracer
    ): TracingInterceptor {
        return OpenObserveInterceptor(
            sdkInstanceName = null,
            tracedHosts = tracedHosts,
            tracedRequestListener = mockRequestListener,
            rumResourceAttributesProvider = mockRumAttributesProvider,
            traceSampler = mockTraceSampler,
            traceContextInjection = TraceContextInjection.ALL,
            redacted404ResourceName = fakeRedacted404Resources,
            localTracerFactory = localTracerFactory,
            globalTracerProvider = globalTracerProvider
        )
    }

    override fun getExpectedOrigin(): String {
        return OpenObserveInterceptor.ORIGIN_RUM
    }

    @Test
    fun `M warn that RUM is not enabled W intercept()`(
        @IntForgery(min = 200, max = 300) statusCode: Int
    ) {
        // Given
        stubChain(mockChain, statusCode)
        // When
        testedInterceptor.intercept(mockChain)

        // Then
        mockInternalLogger.verifyLog(
            InternalLogger.Level.INFO,
            InternalLogger.Target.USER,
            OpenObserveInterceptor.WARN_RUM_DISABLED.format(Locale.US, "Default SDK instance")
        )
    }

    @Test
    fun `M do nothing W intercept() for successful request`(
        @IntForgery(min = 200, max = 300) statusCode: Int
    ) {
        // Given
        stubChain(mockChain, statusCode)

        // When
        testedInterceptor.intercept(mockChain)

        // Then
        verify(rumMonitor.mockInstance).notifyInterceptorInstantiated()
        verify(rumMonitor.mockInstance).reportNetworkingLibraryType(
            InternalTelemetryEvent.ApiUsage.NetworkInstrumentation.LibraryType.LEGACY_OKHTTP
        )
        verifyNoMoreInteractions(rumMonitor.mockInstance)
        verifyNoInteractions(mockRumAttributesProvider)
    }

    @Test
    fun `M do nothing RUM Resource W intercept() for failing request`(
        @IntForgery(min = 400, max = 500) statusCode: Int
    ) {
        // Given
        stubChain(mockChain, statusCode)

        // When
        testedInterceptor.intercept(mockChain)

        // Then
        verify(rumMonitor.mockInstance).notifyInterceptorInstantiated()
        verify(rumMonitor.mockInstance).reportNetworkingLibraryType(
            InternalTelemetryEvent.ApiUsage.NetworkInstrumentation.LibraryType.LEGACY_OKHTTP
        )
        verifyNoMoreInteractions(rumMonitor.mockInstance)
        verifyNoInteractions(mockRumAttributesProvider)
    }

    @Test
    fun `M do nothing W intercept() for throwing request`(
        @Forgery throwable: Throwable
    ) {
        // Given
        whenever(mockChain.request()) doReturn fakeRequest
        whenever(mockChain.proceed(any())) doThrow throwable

        // When
        assertThrows<Throwable>(throwable.message.orEmpty()) {
            testedInterceptor.intercept(mockChain)
        }

        // Then
        verify(rumMonitor.mockInstance).notifyInterceptorInstantiated()
        verify(rumMonitor.mockInstance).reportNetworkingLibraryType(
            InternalTelemetryEvent.ApiUsage.NetworkInstrumentation.LibraryType.LEGACY_OKHTTP
        )
        verifyNoMoreInteractions(rumMonitor.mockInstance)
        verifyNoInteractions(mockRumAttributesProvider)
    }
}
