/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sqldelight

import com.openobserve.android.trace.GlobalOpenObserveTracer
import com.openobserve.android.trace.api.scope.OpenObserveScope
import com.openobserve.android.trace.api.span.OpenObserveSpan
import com.openobserve.android.trace.api.span.OpenObserveSpanBuilder
import com.openobserve.android.trace.api.tracer.OpenObserveTracer
import com.openobserve.tools.unit.forge.BaseConfigurator
import com.squareup.sqldelight.Transacter
import com.squareup.sqldelight.TransactionWithReturn
import com.squareup.sqldelight.TransactionWithoutReturn
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.annotation.Forgery
import fr.xgouchet.elmyr.annotation.StringForgery
import fr.xgouchet.elmyr.junit5.ForgeConfiguration
import fr.xgouchet.elmyr.junit5.ForgeExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness

@Extensions(
    ExtendWith(MockitoExtension::class),
    ExtendWith(ForgeExtension::class)
)
@ForgeConfiguration(value = BaseConfigurator::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SqlDelightExtTest {
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

    @Mock
    lateinit var mockTransacter: Transacter

    @Forgery
    lateinit var fakeException: Throwable

    @Mock
    lateinit var mockTransactionWithReturn: TransactionWithReturn<Any>

    @Mock
    lateinit var mockTransactionWithoutReturn: TransactionWithoutReturn

    @BeforeEach
    fun `set up`() {
        GlobalOpenObserveTracer.registerIfAbsent(mockTracer)
        whenever(mockTracer.buildSpan(fakeOperationName)) doReturn mockSpanBuilder
        whenever(mockTracer.activateSpan(mockSpan)) doReturn mockScope
        whenever(mockSpanBuilder.start()) doReturn mockSpan
        doAnswer {
            it.getArgument<TransactionWithoutReturn.() -> Unit>(1).invoke(
                mockTransactionWithoutReturn
            )
        }.whenever(mockTransacter).transaction(any(), any())
        doAnswer {
            it.getArgument<TransactionWithReturn<Any>.() -> Boolean>(1).invoke(
                mockTransactionWithReturn
            )
        }.whenever(mockTransacter)
            .transactionWithResult(any(), any<TransactionWithReturn<Any>.() -> Any>())
    }

    @AfterEach
    fun `tear down`() {
        GlobalOpenObserveTracer.clear()
    }

    @Test
    fun `M create Span around transaction W transactionTraced() {nonEnclosing = true}`() {
        // GIVEN
        whenever(mockTracer.activeSpan()) doReturn mockParentSpan
        whenever(mockSpanBuilder.withParentSpan(mockParentSpan)) doReturn mockSpanBuilder
        var transactionExecuted = false
        val body: TransactionWithoutReturn.() -> Unit = {
            transactionExecuted = true
        }

        // WHEN
        mockTransacter.transactionTraced(fakeOperationName, true, body)

        // THEN
        assertThat(transactionExecuted).isTrue()
        verify(mockSpanBuilder).withParentSpan(mockParentSpan)
        inOrder(mockSpan, mockScope) {
            verify(mockSpan).finish()
            verify(mockScope).close()
        }
        verify(mockTransacter).transaction(eq(true), any())
        verifyNoMoreInteractions(mockTransacter)
    }

    @Test
    fun `M create Span around transaction W transactionTraced() {nonEnclosing = false}`() {
        // GIVEN
        whenever(mockTracer.activeSpan()) doReturn mockParentSpan
        whenever(mockSpanBuilder.withParentSpan(mockParentSpan)) doReturn mockSpanBuilder
        var transactionExecuted = false
        val body: TransactionWithoutReturn.() -> Unit = {
            transactionExecuted = true
        }

        // WHEN
        mockTransacter.transactionTraced(fakeOperationName, false, body)

        // THEN
        assertThat(transactionExecuted).isTrue()
        verify(mockSpanBuilder).withParentSpan(mockParentSpan)
        inOrder(mockSpan, mockScope) {
            verify(mockSpan).finish()
            verify(mockScope).close()
        }
        verify(mockTransacter).transaction(eq(false), any())
        verifyNoMoreInteractions(mockTransacter)
    }

    @Test
    fun `M create Span around transaction W transactionTraced() without parents`(forge: Forge) {
        // GIVEN
        val fakeNoEnclosing = forge.aBool()
        whenever(mockTracer.activeSpan()) doReturn null
        whenever(mockSpanBuilder.withParentSpan(null as OpenObserveSpan?)) doReturn mockSpanBuilder
        var transactionExecuted = false
        val body: TransactionWithSpanAndWithoutReturn.() -> Unit = {
            transactionExecuted = true
        }

        // WHEN
        mockTransacter.transactionTraced(fakeOperationName, fakeNoEnclosing, body)

        // THEN
        assertThat(transactionExecuted).isTrue()
        verify(mockSpanBuilder).withParentSpan(null as OpenObserveSpan?)
        inOrder(mockSpan, mockScope) {
            verify(mockSpan).finish()
            verify(mockScope).close()
        }
        verify(mockTransacter).transaction(eq(fakeNoEnclosing), any())
        verifyNoMoreInteractions(mockTransacter)
    }

    @Test
    fun `M close the Span around transaction W transactionTraced() throws exception`(forge: Forge) {
        // GIVEN
        val fakeNoEnclosing = forge.aBool()
        var caughtException: Throwable? = null
        whenever(mockTracer.activeSpan()) doReturn null
        whenever(mockSpanBuilder.withParentSpan(null as OpenObserveSpan?)) doReturn mockSpanBuilder

        // WHEN
        try {
            mockTransacter.transactionTraced(fakeOperationName, fakeNoEnclosing) {
                throw fakeException
            }
        } catch (e: Throwable) {
            caughtException = fakeException
        }

        // THEN
        assertThat(caughtException).isEqualTo(fakeException)
        verify(mockSpanBuilder).withParentSpan(null as OpenObserveSpan?)
        inOrder(mockSpan, mockScope) {
            verify(mockSpan).addThrowable(fakeException)
            verify(mockSpan).finish()
            verify(mockScope).close()
        }
    }

    @Test
    fun `M log the exception W transactionTraced() throws exception`(forge: Forge) {
        // GIVEN
        val fakeNoEnclosing = forge.aBool()
        var caughtException: Throwable? = null
        whenever(mockTracer.activeSpan()) doReturn null
        whenever(mockSpanBuilder.withParentSpan(null as OpenObserveSpan?)) doReturn mockSpanBuilder

        // WHEN
        try {
            mockTransacter.transactionTraced(fakeOperationName, fakeNoEnclosing) {
                throw fakeException
            }
        } catch (e: Throwable) {
            caughtException = fakeException
        }

        // THEN
        assertThat(caughtException).isEqualTo(fakeException)
        verify(mockSpanBuilder).withParentSpan(null as OpenObserveSpan?)
        verify(mockSpan).addThrowable(fakeException)
    }

    @Test
    fun `M execute the transaction W transactionTraced()`(forge: Forge) {
        // GIVEN
        val fakeNoEnclosing = forge.aBool()
        whenever(mockTracer.activeSpan()) doReturn null
        whenever(mockSpanBuilder.withParentSpan(null as OpenObserveSpan?)) doReturn mockSpanBuilder
        var transactionExecuted = false
        val afterCommitLambda = {
        }
        val body: TransactionWithSpanAndWithoutReturn.() -> Unit = {
            transactionExecuted = true
            afterCommit(afterCommitLambda)
        }

        // WHEN
        mockTransacter.transactionTraced(fakeOperationName, fakeNoEnclosing, body)

        // THEN
        assertThat(transactionExecuted).isTrue()
        verify(mockTransactionWithoutReturn).afterCommit(afterCommitLambda)
    }

    @Test
    fun `M add Span tags W transactionTraced()`(forge: Forge) {
        // GIVEN
        val fakeTagKey = forge.anAlphabeticalString()
        val fakeTagValue = forge.anAlphabeticalString()
        val fakeNoEnclosing = forge.aBool()
        whenever(mockTracer.activeSpan()) doReturn null
        whenever(mockSpanBuilder.withParentSpan(null as OpenObserveSpan?)) doReturn mockSpanBuilder
        var transactionExecuted = false
        val body: TransactionWithSpanAndWithoutReturn.() -> Unit = {
            transactionExecuted = true
            setTag(fakeTagKey, fakeTagValue)
        }

        // WHEN
        mockTransacter.transactionTraced(fakeOperationName, fakeNoEnclosing, body)

        // THEN
        assertThat(transactionExecuted).isTrue()
        verify(mockSpan).setTag(fakeTagKey, fakeTagValue)
    }

    @Test
    fun `M create Span around W transactionTracedWithResult() {noEnclosing = true}`() {
        // // GIVEN
        whenever(mockTracer.activeSpan()) doReturn mockParentSpan
        whenever(mockSpanBuilder.withParentSpan(mockParentSpan)) doReturn mockSpanBuilder
        val body: TransactionWithSpanAndWithReturn<Boolean>.() -> Boolean = {
            true
        }

        // WHEN
        val transactionExecuted =
            mockTransacter.transactionTracedWithResult(fakeOperationName, true, body)

        // THEN
        assertThat(transactionExecuted).isTrue()
        verify(mockSpanBuilder).withParentSpan(mockParentSpan)
        inOrder(mockSpan, mockScope) {
            verify(mockSpan).finish()
            verify(mockScope).close()
        }
        verify(mockTransacter).transactionWithResult(
            eq(true),
            any<TransactionWithReturn<Boolean>.() -> Boolean>()
        )
        verifyNoMoreInteractions(mockTransacter)
    }

    @Test
    fun `M create Span around W transactionTracedWithResult() {noEnclosing = false}`() {
        // GIVEN
        whenever(mockTracer.activeSpan()) doReturn mockParentSpan
        whenever(mockSpanBuilder.withParentSpan(mockParentSpan)) doReturn mockSpanBuilder
        val body: TransactionWithSpanAndWithReturn<Boolean>.() -> Boolean = {
            true
        }

        // WHEN
        val transactionExecuted =
            mockTransacter.transactionTracedWithResult(fakeOperationName, false, body)

        // THEN
        assertThat(transactionExecuted).isTrue()
        verify(mockSpanBuilder).withParentSpan(mockParentSpan)
        inOrder(mockSpan, mockScope) {
            verify(mockSpan).finish()
            verify(mockScope).close()
        }
        verify(mockTransacter).transactionWithResult(
            eq(false),
            any<TransactionWithReturn<Boolean>.() -> Boolean>()
        )
        verifyNoMoreInteractions(mockTransacter)
    }

    @Test
    fun `M create Span around W transactionTracedWithResult() without parents`(forge: Forge) {
        // GIVEN
        val fakeNoEnclosing = forge.aBool()
        whenever(mockTracer.activeSpan()) doReturn null
        whenever(mockSpanBuilder.withParentSpan(null as OpenObserveSpan?)) doReturn mockSpanBuilder
        val body: TransactionWithSpanAndWithReturn<Boolean>.() -> Boolean = {
            true
        }

        // WHEN
        val transactionExecuted =
            mockTransacter.transactionTracedWithResult(fakeOperationName, fakeNoEnclosing, body)

        // THEN
        assertThat(transactionExecuted).isTrue()
        verify(mockSpanBuilder).withParentSpan(null as OpenObserveSpan?)
        inOrder(mockSpan, mockScope) {
            verify(mockSpan).finish()
            verify(mockScope).close()
        }
        verify(mockTransacter).transactionWithResult(
            eq(fakeNoEnclosing),
            any<TransactionWithReturn<Boolean>.() -> Boolean>()
        )
        verifyNoMoreInteractions(mockTransacter)
    }

    @Test
    fun `M close the Span W transactionTracedWithResult() throws exception`(forge: Forge) {
        // GIVEN
        val fakeNoEnclosing = forge.aBool()
        var caughtException: Throwable? = null
        whenever(mockTracer.activeSpan()) doReturn null
        whenever(mockSpanBuilder.withParentSpan(null as OpenObserveSpan?)) doReturn mockSpanBuilder

        // WHEN
        try {
            mockTransacter.transactionTracedWithResult<Transacter, Boolean>(
                fakeOperationName,
                noEnclosing = fakeNoEnclosing
            ) {
                throw fakeException
            }
        } catch (e: Throwable) {
            caughtException = fakeException
        }

        // THEN
        assertThat(caughtException).isEqualTo(fakeException)
        verify(mockSpanBuilder).withParentSpan(null as OpenObserveSpan?)
        inOrder(mockSpan, mockScope) {
            verify(mockSpan).addThrowable(fakeException)
            verify(mockSpan).finish()
            verify(mockScope).close()
        }
    }

    @Test
    fun `M log the exception W transactionTracedWithResult() throws exception`(forge: Forge) {
        // GIVEN
        val fakeNoEnclosing = forge.aBool()
        var caughtException: Throwable? = null
        whenever(mockTracer.activeSpan()) doReturn null
        whenever(mockSpanBuilder.withParentSpan(null as OpenObserveSpan?)) doReturn mockSpanBuilder

        // WHEN
        try {
            mockTransacter.transactionTracedWithResult<Transacter, Boolean>(
                fakeOperationName,
                noEnclosing = fakeNoEnclosing
            ) {
                throw fakeException
            }
        } catch (e: Throwable) {
            caughtException = fakeException
        }

        // THEN
        assertThat(caughtException).isEqualTo(fakeException)
        verify(mockSpanBuilder).withParentSpan(null as OpenObserveSpan?)
        verify(mockSpan).addThrowable(fakeException)
    }

    @Test
    fun `M execute the transaction W transactionTracedWithResult()`(forge: Forge) {
        // GIVEN
        val fakeNoEnclosing = forge.aBool()
        whenever(mockTracer.activeSpan()) doReturn null
        whenever(mockSpanBuilder.withParentSpan(null as OpenObserveSpan?)) doReturn mockSpanBuilder
        val afterCommitLambda = {
        }
        val body: TransactionWithSpanAndWithReturn<Boolean>.() -> Boolean = {
            afterCommit(afterCommitLambda)
            true
        }

        // WHEN
        val transactionExecuted =
            mockTransacter.transactionTracedWithResult(fakeOperationName, fakeNoEnclosing, body)

        // THEN
        assertThat(transactionExecuted).isTrue()
        verify(mockTransactionWithReturn).afterCommit(afterCommitLambda)
    }

    @Test
    fun `M add Span tags W transactionTracedWithResult()`(forge: Forge) {
        // GIVEN
        val fakeTagKey = forge.anAlphabeticalString()
        val fakeTagValue = forge.anAlphabeticalString()
        val fakeNoEnclosing = forge.aBool()
        whenever(mockTracer.activeSpan()) doReturn null
        whenever(mockSpanBuilder.withParentSpan(null as OpenObserveSpan?)) doReturn mockSpanBuilder
        val body: TransactionWithSpanAndWithReturn<Boolean>.() -> Boolean = {
            setTag(fakeTagKey, fakeTagValue)
            true
        }

        // WHEN
        val transactionExecuted =
            mockTransacter.transactionTracedWithResult(fakeOperationName, fakeNoEnclosing, body)

        // THEN
        assertThat(transactionExecuted).isTrue()
        verify(mockSpan).setTag(fakeTagKey, fakeTagValue)
    }
}
