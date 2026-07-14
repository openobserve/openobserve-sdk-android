/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.trace

import com.openobserve.android.trace.api.scope.OpenObserveScope
import com.openobserve.android.trace.api.span.OpenObserveSpan
import com.openobserve.android.trace.api.span.OpenObserveSpanBuilder
import com.openobserve.android.trace.api.tracer.OpenObserveTracer
import com.openobserve.tools.unit.forge.BaseConfigurator
import fr.xgouchet.elmyr.annotation.Forgery
import fr.xgouchet.elmyr.annotation.LongForgery
import fr.xgouchet.elmyr.annotation.StringForgery
import fr.xgouchet.elmyr.junit5.ForgeConfiguration
import fr.xgouchet.elmyr.junit5.ForgeExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness

@Extensions(
    ExtendWith(MockitoExtension::class),
    ExtendWith(ForgeExtension::class)
)
@MockitoSettings(strictness = Strictness.LENIENT)
@ForgeConfiguration(BaseConfigurator::class)
class SpanExtTest {

    @Mock
    lateinit var mockTracer: OpenObserveTracer

    @Mock
    lateinit var mockSpanBuilder: OpenObserveSpanBuilder

    @Mock
    lateinit var mockSpan: OpenObserveSpan

    @Mock
    lateinit var mockParentSpan: OpenObserveSpan

    @Mock
    lateinit var mockScope: OpenObserveScope

    @StringForgery
    lateinit var fakeOperationName: String

    @BeforeEach
    fun `set up`() {
        GlobalOpenObserveTracer.registerIfAbsent(mockTracer)
        whenever(mockTracer.buildSpan(fakeOperationName)) doReturn mockSpanBuilder
        whenever(mockTracer.activateSpan(mockSpan)) doReturn mockScope
        whenever(mockSpanBuilder.withParentSpan(mockParentSpan)) doReturn mockSpanBuilder
        whenever(mockSpanBuilder.start()) doReturn mockSpan
    }

    @AfterEach
    fun `tear down`() {
        GlobalOpenObserveTracer.clear()
    }

    @Test
    fun `M create span around lambda W withinSpan(name){}`(
        @LongForgery result: Long
    ) {
        var lambdaCalled = false
        whenever(mockSpanBuilder.withParentSpan(null as OpenObserveSpan?)) doReturn mockSpanBuilder

        val callResult = withinSpan(fakeOperationName) {
            lambdaCalled = true
            result
        }

        assertThat(lambdaCalled).isTrue()
        assertThat(callResult).isEqualTo(result)
        verify(mockSpanBuilder).withParentSpan(null as OpenObserveSpan?)
        inOrder(mockSpan, mockScope) {
            verify(mockSpan).finish()
            verify(mockScope).close()
        }
    }

    @Test
    fun `M create span and scope around lambda W withinSpan(name, parent){}`(
        @LongForgery result: Long
    ) {
        var lambdaCalled = false

        val callResult = withinSpan(fakeOperationName, mockParentSpan) {
            lambdaCalled = true
            result
        }

        assertThat(lambdaCalled).isTrue()
        assertThat(callResult).isEqualTo(result)
        verify(mockSpanBuilder).withParentSpan(mockParentSpan)
        inOrder(mockSpan, mockScope) {
            verify(mockSpan).finish()
            verify(mockScope).close()
        }
    }

    @Test
    fun `M create span and scope around lambda W withinSpan(name, parent){} throwing error`(
        @Forgery throwable: Throwable
    ) {
        var lambdaCalled = false

        val thrown = assertThrows<Throwable> {
            withinSpan(fakeOperationName, mockParentSpan) {
                lambdaCalled = true
                throw throwable
            }
        }

        assertThat(thrown).isEqualTo(throwable)
        assertThat(lambdaCalled).isTrue()
        verify(mockSpanBuilder).withParentSpan(mockParentSpan)
        inOrder(mockSpan, mockScope) {
            verify(mockSpan).logThrowable(throwable)
            verify(mockSpan).finish()
            verify(mockScope).close()
        }
    }

    @Test
    fun `M create span around lambda W withinSpan(name, parent, false){}`(
        @LongForgery result: Long
    ) {
        var lambdaCalled = false

        val callResult = withinSpan(fakeOperationName, mockParentSpan, false) {
            lambdaCalled = true
            result
        }

        assertThat(lambdaCalled).isTrue()
        assertThat(callResult).isEqualTo(result)
        verify(mockSpanBuilder).withParentSpan(mockParentSpan)
        inOrder(mockSpan) {
            verify(mockSpan).finish()
        }
        verify(mockTracer, never()).activateSpan(mockSpan)
    }
}
