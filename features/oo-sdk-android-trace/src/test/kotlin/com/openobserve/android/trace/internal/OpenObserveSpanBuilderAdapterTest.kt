/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */
package com.openobserve.android.trace.internal

import com.openobserve.android.trace.api.span.OpenObserveSpan
import com.openobserve.android.trace.api.span.OpenObserveSpanLink
import com.openobserve.android.utils.forge.Configurator
import com.openobserve.trace.bootstrap.instrumentation.api.AgentSpan
import com.openobserve.trace.bootstrap.instrumentation.api.AgentSpanLink
import com.openobserve.trace.bootstrap.instrumentation.api.AgentTracer
import fr.xgouchet.elmyr.annotation.DoubleForgery
import fr.xgouchet.elmyr.annotation.Forgery
import fr.xgouchet.elmyr.annotation.LongForgery
import fr.xgouchet.elmyr.annotation.StringForgery
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
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness

@Extensions(
    ExtendWith(MockitoExtension::class),
    ExtendWith(ForgeExtension::class)
)
@MockitoSettings(strictness = Strictness.LENIENT)
@ForgeConfiguration(Configurator::class)
class OpenObserveSpanBuilderAdapterTest {

    @StringForgery
    lateinit var fakeString: String

    @LongForgery
    private var fakeLong: Long = 0

    @DoubleForgery
    private var fakeDouble: Double = 0.0

    @Forgery
    lateinit var fakeOpenObserveSpanLink: OpenObserveSpanLink

    @Mock
    private lateinit var mockAgentSpanBuilderAdapter: AgentTracer.SpanBuilder

    @Mock
    private lateinit var mockAgentSpanContext: AgentSpan.Context

    @Mock
    private lateinit var mockSpanLogger: OpenObserveSpanLogger

    private lateinit var testedBuilderAdapter: OpenObserveSpanBuilderAdapter

    @BeforeEach
    fun `set up`() {
        testedBuilderAdapter = OpenObserveSpanBuilderAdapter(mockAgentSpanBuilderAdapter, mockSpanLogger)
    }

    @Test
    fun `M delegate ignoreActiveSpan W ignoreActiveSpan is called`() {
        // When
        testedBuilderAdapter.ignoreActiveSpan()

        // Then
        verify(mockAgentSpanBuilderAdapter).ignoreActiveSpan()
    }

    @Test
    fun `M return OpenObserveSpan W start() is called`() {
        // Given
        whenever(mockAgentSpanBuilderAdapter.start()).thenReturn(mock<AgentSpan>())

        // When
        val actual = testedBuilderAdapter.start()

        // Then
        assertThat(actual).isInstanceOf(OpenObserveSpan::class.java)
    }

    @Test
    fun `M delegate withOrigin W withOrigin is called`() {
        // When
        val actual = testedBuilderAdapter.withOrigin(fakeString)

        // Then
        assertThat(actual).isEqualTo(testedBuilderAdapter)
        verify(mockAgentSpanBuilderAdapter).withOrigin(fakeString)
    }

    @Test
    fun `M delegate withStartTimestamp W withStartTimestamp is called`() {
        // When
        val actual = testedBuilderAdapter.withStartTimestamp(fakeLong)

        // Then
        assertThat(actual).isEqualTo(testedBuilderAdapter)
        verify(mockAgentSpanBuilderAdapter).withStartTimestamp(fakeLong)
    }

    @Test
    fun `M delegate withTag W withTag(String, Long) is called`() {
        // When
        testedBuilderAdapter.withTag(fakeString, fakeLong)

        // Then
        verify(mockAgentSpanBuilderAdapter).withTag(fakeString, fakeLong)
    }

    @Test
    fun `M delegate withTag W withTag(String, Any) is called`() {
        // Given
        val value = Any()

        // When
        testedBuilderAdapter.withTag(fakeString, value)

        // Then
        verify(mockAgentSpanBuilderAdapter).withTag(fakeString, value)
    }

    @Test
    fun `M delegate withTag W withTag(String, Double) is called`() {
        // When
        testedBuilderAdapter.withTag(fakeString, fakeDouble)

        // Then
        verify(mockAgentSpanBuilderAdapter).withTag(fakeString, fakeDouble)
    }

    @Test
    fun `M delegate withTag W withLink(OpenObserveSpanLink) is called`() {
        // When
        testedBuilderAdapter.withLink(fakeOpenObserveSpanLink)

        // Then
        argumentCaptor<AgentSpanLink> {
            verify(mockAgentSpanBuilderAdapter).withLink(capture())
            assertThat(firstValue.spanId()).isEqualTo(fakeOpenObserveSpanLink.spanId)
            assertThat(firstValue.traceId().toString()).isEqualTo(fakeOpenObserveSpanLink.traceId.toString())
            assertThat(firstValue.attributes().asMap()).isEqualTo(fakeOpenObserveSpanLink.attributes)
        }
    }

    @Test
    fun `M delegate withResourceName W withResourceName(String) is called`() {
        // When
        testedBuilderAdapter.withResourceName(fakeString)

        // Then
        verify(mockAgentSpanBuilderAdapter).withResourceName(fakeString)
    }

    @Test
    fun `M delegate asChildOf W withParentContext(OpenObserveSpanContextAdapter) is called`() {
        // When
        testedBuilderAdapter.withParentContext(OpenObserveSpanContextAdapter(mockAgentSpanContext))

        // Then
        verify(mockAgentSpanBuilderAdapter).asChildOf(mockAgentSpanContext)
    }

    @Test
    fun `M delegate asChildOf W withParentSpan(OpenObserveSpanContextAdapter) is called`() {
        // Given
        val mockSpan = mock<OpenObserveSpan> {
            on { context() } doReturn OpenObserveSpanContextAdapter(mockAgentSpanContext)
        }

        // When
        testedBuilderAdapter.withParentSpan(mockSpan)

        // Then
        verify(mockAgentSpanBuilderAdapter).asChildOf(mockAgentSpanContext)
    }
}
