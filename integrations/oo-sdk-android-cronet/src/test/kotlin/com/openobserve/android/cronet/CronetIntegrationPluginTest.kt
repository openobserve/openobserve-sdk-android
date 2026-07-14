/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.cronet

import com.openobserve.android.api.InternalLogger
import com.openobserve.android.cronet.internal.OpenObserveCronetEngine
import com.openobserve.android.rum.ExperimentalRumApi
import com.openobserve.android.rum.configuration.RumNetworkInstrumentationConfiguration
import com.openobserve.android.tests.config.OpenObserveSingletonTestConfiguration
import com.openobserve.android.tests.elmyr.aHostName
import com.openobserve.android.trace.ApmNetworkInstrumentationConfiguration
import com.openobserve.android.trace.ApmNetworkTracingScope
import com.openobserve.android.trace.ExperimentalTraceApi
import com.openobserve.android.utils.forge.Configurator
import com.openobserve.tools.unit.annotations.TestConfigurationsProvider
import com.openobserve.tools.unit.extensions.TestConfigurationExtension
import com.openobserve.tools.unit.extensions.config.TestConfiguration
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.annotation.StringForgery
import fr.xgouchet.elmyr.junit5.ForgeConfiguration
import fr.xgouchet.elmyr.junit5.ForgeExtension
import org.assertj.core.api.Assertions.assertThat
import org.chromium.net.CronetEngine
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness
import java.util.concurrent.Executor

@Extensions(
    ExtendWith(MockitoExtension::class),
    ExtendWith(ForgeExtension::class),
    ExtendWith(TestConfigurationExtension::class)
)
@MockitoSettings(strictness = Strictness.LENIENT)
@ForgeConfiguration(Configurator::class)
@OptIn(
    ExperimentalRumApi::class,
    ExperimentalTraceApi::class
)
internal class CronetIntegrationPluginTest {

    @Mock
    lateinit var mockDelegateBuilder: CronetEngine.Builder

    @Mock
    lateinit var mockCronetEngine: CronetEngine

    @Mock
    lateinit var mockInternalLogger: InternalLogger

    private lateinit var fakeTracedHost: List<String>

    @BeforeEach
    fun `set up`(forge: Forge) {
        fakeTracedHost = listOf(forge.aHostName())
        whenever(mockDelegateBuilder.build()) doReturn mockCronetEngine
        whenever(ooCore.mockInstance.internalLogger) doReturn mockInternalLogger
    }

    @Test
    fun `M return plain CronetEngine W build() {both configs null}`() {
        // When
        val result = mockDelegateBuilder
            .configureOpenObserveInstrumentation(
                rumInstrumentationConfiguration = null,
                apmInstrumentationConfiguration = null
            )
            .build()

        // Then
        assertThat(result).isSameAs(mockCronetEngine)
        assertThat(result).isNotInstanceOf(OpenObserveCronetEngine::class.java)
    }

    @Test
    fun `M return OpenObserveCronetEngine W build() {APM config only}`() {
        // When
        val engine = mockDelegateBuilder
            .configureOpenObserveInstrumentation(
                rumInstrumentationConfiguration = null,
                apmInstrumentationConfiguration = ApmNetworkInstrumentationConfiguration(fakeTracedHost)
            )
            .build()

        // Then
        check(engine is OpenObserveCronetEngine)
        assertThat(engine.apmNetworkInstrumentation).isNotNull
        assertThat(engine.rumNetworkInstrumentation).isNull()
        assertThat(engine.distributedTracingInstrumentation).isNull()
    }

    @Test
    fun `M return OpenObserveCronetEngine W build() {RUM config only}`() {
        // When
        val engine = mockDelegateBuilder
            .configureOpenObserveInstrumentation(
                rumInstrumentationConfiguration = RumNetworkInstrumentationConfiguration(),
                apmInstrumentationConfiguration = null
            )
            .build()

        // Then
        check(engine is OpenObserveCronetEngine)
        assertThat(engine.apmNetworkInstrumentation).isNull()
        assertThat(engine.rumNetworkInstrumentation).isNotNull
        assertThat(engine.distributedTracingInstrumentation).isNull()
    }

    @Test
    fun `M return OpenObserveCronetEngine W build() {both configs}`() {
        // When
        val engine = mockDelegateBuilder
            .configureOpenObserveInstrumentation(
                rumInstrumentationConfiguration = RumNetworkInstrumentationConfiguration(),
                apmInstrumentationConfiguration = ApmNetworkInstrumentationConfiguration(fakeTracedHost)
            ).build()

        // Then
        check(engine is OpenObserveCronetEngine)
        assertThat(engine.apmNetworkInstrumentation).isNotNull
        assertThat(engine.rumNetworkInstrumentation).isNotNull
        assertThat(engine.distributedTracingInstrumentation).isNotNull
    }

    @Test
    fun `M create distributedTracingInstrumentation W build() {RUM + APM configs}`() {
        // When
        val engine = mockDelegateBuilder
            .configureOpenObserveInstrumentation(
                rumInstrumentationConfiguration = RumNetworkInstrumentationConfiguration(),
                apmInstrumentationConfiguration = ApmNetworkInstrumentationConfiguration(fakeTracedHost)
            ).build()

        // Then
        check(engine is OpenObserveCronetEngine)
        assertThat(engine.distributedTracingInstrumentation).isNotNull
        assertThat(engine.distributedTracingInstrumentation?.traceOrigin)
            .isEqualTo(CronetIntegrationPlugin.ORIGIN_RUM)
        assertThat(engine.distributedTracingInstrumentation?.networkTracingScope)
            .isEqualTo(ApmNetworkTracingScope.EXCLUDE_INTERNAL_REDIRECTS)
    }

    @Test
    fun `M not create distributedTracingInstrumentation W build() {APM only, no RUM}`() {
        // When
        val engine = mockDelegateBuilder
            .configureOpenObserveInstrumentation(
                rumInstrumentationConfiguration = null,
                apmInstrumentationConfiguration = ApmNetworkInstrumentationConfiguration(fakeTracedHost)
            ).build()

        // Then
        check(engine is OpenObserveCronetEngine)
        assertThat(engine.distributedTracingInstrumentation).isNull()
    }

    @Test
    fun `M use custom executor W build() {setListenerExecutor called}`() {
        // Given
        val mockExecutor = mock<Executor>()

        // When
        val engine = mockDelegateBuilder
            .configureOpenObserveInstrumentation(
                rumInstrumentationConfiguration = RumNetworkInstrumentationConfiguration(),
                apmInstrumentationConfiguration = null
            )
            .setListenerExecutor(mockExecutor)
            .build()

        // Then
        assertThat(engine).isInstanceOf(OpenObserveCronetEngine::class.java)
    }

    @Test
    fun `M set traceOrigin to rum on distributedTracing W build() {RUM + APM}`() {
        // When
        val engine = mockDelegateBuilder.configureOpenObserveInstrumentation(
            rumInstrumentationConfiguration = RumNetworkInstrumentationConfiguration(),
            apmInstrumentationConfiguration = ApmNetworkInstrumentationConfiguration(fakeTracedHost)
        ).build()

        // Then
        check(engine is OpenObserveCronetEngine)
        assertThat(engine.distributedTracingInstrumentation?.traceOrigin)
            .isEqualTo(CronetIntegrationPlugin.ORIGIN_RUM)
    }

    @Test
    fun `M not set traceOrigin on apmInstrumentation W build() {RUM + APM}`() {
        // When
        val engine = mockDelegateBuilder.configureOpenObserveInstrumentation(
            rumInstrumentationConfiguration = RumNetworkInstrumentationConfiguration(),
            apmInstrumentationConfiguration = ApmNetworkInstrumentationConfiguration(fakeTracedHost)
        ).build()

        // Then
        check(engine is OpenObserveCronetEngine)
        assertThat(engine.apmNetworkInstrumentation?.traceOrigin).isNull()
    }

    @Test
    fun `M not create apmInstrumentation W build() {setHeaderPropagationOnly}`() {
        // When
        val engine = mockDelegateBuilder
            .configureOpenObserveInstrumentation(
                rumInstrumentationConfiguration = RumNetworkInstrumentationConfiguration(),
                apmInstrumentationConfiguration = ApmNetworkInstrumentationConfiguration(fakeTracedHost)
                    .setHeaderPropagationOnly()
            ).build()

        // Then
        check(engine is OpenObserveCronetEngine)
        assertThat(engine.apmNetworkInstrumentation).isNull()
        assertThat(engine.distributedTracingInstrumentation).isNotNull
    }

    @Test
    fun `M create both instrumentations W build() {canSendSpan is true by default}`() {
        // When
        val engine = mockDelegateBuilder
            .configureOpenObserveInstrumentation(
                rumInstrumentationConfiguration = RumNetworkInstrumentationConfiguration(),
                apmInstrumentationConfiguration = ApmNetworkInstrumentationConfiguration(fakeTracedHost)
            ).build()

        // Then
        check(engine is OpenObserveCronetEngine)
        assertThat(engine.distributedTracingInstrumentation).isNotNull
        assertThat(engine.distributedTracingInstrumentation?.traceOrigin).isEqualTo(CronetIntegrationPlugin.ORIGIN_RUM)
    }

    @Test
    fun `M delegate to builder W build() {both configs null}`() {
        // When
        mockDelegateBuilder
            .configureOpenObserveInstrumentation(
                rumInstrumentationConfiguration = null,
                apmInstrumentationConfiguration = null
            )
            .build()

        // Then
        verify(mockDelegateBuilder).build()
    }

    @Test
    fun `M not send network instrumentation telemetry W build {no rumInstrumentation}()`() {
        // When
        mockDelegateBuilder
            .configureOpenObserveInstrumentation(
                rumInstrumentationConfiguration = null,
                apmInstrumentationConfiguration = ApmNetworkInstrumentationConfiguration(fakeTracedHost)
            )
            .build()

        // Then
        verifyNoInteractions(mockInternalLogger)
    }

    @Test
    fun `M change to ORIGIN_RUM on distributedTracing W build() {APM has custom origin}`(
        @StringForgery fakeCustomOrigin: String
    ) {
        // When
        val engine = mockDelegateBuilder
            .configureOpenObserveInstrumentation(
                rumInstrumentationConfiguration = RumNetworkInstrumentationConfiguration(),
                apmInstrumentationConfiguration = ApmNetworkInstrumentationConfiguration(fakeTracedHost)
                    .setTraceOrigin(fakeCustomOrigin)
            ).build()

        // Then
        check(engine is OpenObserveCronetEngine)
        assertThat(engine.distributedTracingInstrumentation?.traceOrigin)
            .isEqualTo(CronetIntegrationPlugin.ORIGIN_RUM)
    }

    @Test
    fun `M override traceScope on distributedTracing W build() {APM has ALL scope}`() {
        // Given
        val apmConfig = ApmNetworkInstrumentationConfiguration(fakeTracedHost)
            .setTraceScope(ApmNetworkTracingScope.ALL)

        // When
        val engine = mockDelegateBuilder
            .configureOpenObserveInstrumentation(
                rumInstrumentationConfiguration = RumNetworkInstrumentationConfiguration(),
                apmInstrumentationConfiguration = apmConfig
            ).build()

        // Then
        check(engine is OpenObserveCronetEngine)
        assertThat(engine.distributedTracingInstrumentation?.networkTracingScope)
            .isEqualTo(ApmNetworkTracingScope.EXCLUDE_INTERNAL_REDIRECTS)
    }

    @Test
    fun `M not modify original APM config scope W build() {RUM + APM}`() {
        // When
        val engine = mockDelegateBuilder
            .configureOpenObserveInstrumentation(
                rumInstrumentationConfiguration = RumNetworkInstrumentationConfiguration(),
                apmInstrumentationConfiguration = ApmNetworkInstrumentationConfiguration(fakeTracedHost)
                    .setTraceScope(ApmNetworkTracingScope.ALL)

            ).build()

        // Then
        check(engine is OpenObserveCronetEngine)
        assertThat(engine.apmNetworkInstrumentation?.networkTracingScope)
            .isEqualTo(ApmNetworkTracingScope.ALL)
    }

    @Test
    fun `M not send telemetry W build() {sdk core not available}`() {
        // Given
        ooCore.clearRegistry()

        // When
        mockDelegateBuilder
            .configureOpenObserveInstrumentation(
                rumInstrumentationConfiguration = null,
                apmInstrumentationConfiguration = null
            )
            .build()

        // Then
        verifyNoInteractions(mockInternalLogger)
    }

    companion object {
        val ooCore = OpenObserveSingletonTestConfiguration()

        @TestConfigurationsProvider
        @JvmStatic
        fun getTestConfigurations(): List<TestConfiguration> {
            return listOf(ooCore)
        }
    }
}
