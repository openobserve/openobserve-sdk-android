/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */
package com.openobserve.android.trace

import com.openobserve.android.api.InternalLogger
import com.openobserve.android.api.feature.Feature
import com.openobserve.android.api.feature.FeatureScope
import com.openobserve.android.api.feature.FeatureSdkCore
import com.openobserve.android.trace.OpenObserveTracing.ErrorMessages.DEFAULT_SERVICE_NAME_IS_MISSING_ERROR_MESSAGE
import com.openobserve.android.trace.OpenObserveTracing.ErrorMessages.TRACING_NOT_ENABLED_ERROR_MESSAGE
import com.openobserve.android.trace.OpenObserveTracing.ErrorMessages.WRITER_PROVIDER_INTERFACE_NOT_IMPLEMENTED_ERROR_MESSAGE
import com.openobserve.android.trace.OpenObserveTracing.ErrorMessages.buildWrongWrapperMessage
import com.openobserve.android.trace.api.span.OpenObserveSpanWriter
import com.openobserve.android.trace.internal.OpenObserveSpanWriterWrapper
import com.openobserve.android.trace.internal.OpenObserveTracerAdapter
import com.openobserve.android.utils.forge.Configurator
import com.openobserve.android.utils.verifyLog
import com.openobserve.tools.unit.getFieldValue
import com.openobserve.trace.common.writer.NoOpWriter
import com.openobserve.trace.common.writer.Writer
import com.openobserve.trace.core.CoreTracer
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.junit5.ForgeConfiguration
import fr.xgouchet.elmyr.junit5.ForgeExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness

@Extensions(
    ExtendWith(MockitoExtension::class),
    ExtendWith(ForgeExtension::class)
)
@MockitoSettings(strictness = Strictness.LENIENT)
@ForgeConfiguration(Configurator::class)
class OpenObserveTracingTest {

    @Mock
    lateinit var mockSdkCore: FeatureSdkCore

    @Mock
    lateinit var mockInternalLogger: InternalLogger

    interface StubTracingFeature : Feature, InternalCoreWriterProvider

    @Mock
    lateinit var mockTracingFeature: FeatureScope

    @Mock
    lateinit var mockTracingFeatureScope: StubTracingFeature

    lateinit var fakeServiceName: String

    @BeforeEach
    fun `set up`(forge: Forge) {
        fakeServiceName = forge.anAlphabeticalString()
        val writerWrapperMock = mock<OpenObserveSpanWriterWrapper> {
            on { delegate } doReturn mock()
        }
        whenever(mockSdkCore.service) doReturn fakeServiceName
        whenever(mockSdkCore.internalLogger) doReturn mockInternalLogger
        whenever(mockTracingFeature.unwrap<Feature>()) doReturn mockTracingFeatureScope
        whenever(mockSdkCore.getFeature(Feature.TRACING_FEATURE_NAME)) doReturn mockTracingFeature
        whenever(mockTracingFeatureScope.getCoreTracerWriter()) doReturn writerWrapperMock
    }

    @Test
    fun `M use a NoOpCoreTracerWriter W build { TracingFeature not enabled }`() {
        // Given
        whenever(mockSdkCore.getFeature(Feature.TRACING_FEATURE_NAME)) doReturn null

        // When
        val tracer = OpenObserveTracing.newTracerBuilder(mockSdkCore).build() as OpenObserveTracerAdapter

        // Then
        val coreTracer = tracer.delegate as CoreTracer
        val writer: Writer = coreTracer.getFieldValue("writer")
        assertThat(writer).isInstanceOf(NoOpWriter::class.java)
    }

    @Test
    fun `M log a maintainer error W build { TracingFeature not implementing InternalCoreWriterProvider }`() {
        // Given
        whenever(mockTracingFeature.unwrap<Feature>()) doReturn mock()

        // When
        val tracer = OpenObserveTracing.newTracerBuilder(mockSdkCore).build()

        // Then
        assertThat(tracer).isNotNull
        mockInternalLogger.verifyLog(
            InternalLogger.Level.ERROR,
            InternalLogger.Target.MAINTAINER,
            WRITER_PROVIDER_INTERFACE_NOT_IMPLEMENTED_ERROR_MESSAGE
        )
    }

    @Test
    fun `M log a user error W build { default service name not available }`() {
        // Given
        whenever(mockSdkCore.service) doReturn ""

        // When
        OpenObserveTracing.newTracerBuilder(mockSdkCore).build()

        // Then
        mockInternalLogger.verifyLog(
            InternalLogger.Level.ERROR,
            InternalLogger.Target.USER,
            DEFAULT_SERVICE_NAME_IS_MISSING_ERROR_MESSAGE
        )
    }

    @Test
    fun `M log a user error W build { writer is null }`() {
        // Given
        class CustomWrapper : OpenObserveSpanWriter

        val customWrapperInstance = CustomWrapper()
        whenever(mockTracingFeatureScope.getCoreTracerWriter()) doReturn customWrapperInstance

        // When
        OpenObserveTracing.newTracerBuilder(mockSdkCore).build()

        // Then
        mockInternalLogger.verifyLog(
            InternalLogger.Level.ERROR,
            InternalLogger.Target.USER,
            buildWrongWrapperMessage(customWrapperInstance.javaClass)
        )
    }

    @Test
    fun `M log a user error W build { TracingFeature not enabled }`() {
        // Given
        whenever(mockSdkCore.getFeature(Feature.TRACING_FEATURE_NAME)) doReturn null

        // When
        val tracer = OpenObserveTracing.newTracerBuilder(mockSdkCore).build()

        // Then
        assertThat(tracer).isNotNull
        mockInternalLogger.verifyLog(
            InternalLogger.Level.ERROR,
            InternalLogger.Target.USER,
            TRACING_NOT_ENABLED_ERROR_MESSAGE
        )
    }
}
