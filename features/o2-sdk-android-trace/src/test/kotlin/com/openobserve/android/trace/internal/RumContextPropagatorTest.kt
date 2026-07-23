/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */
package com.openobserve.android.trace.internal

import com.openobserve.android.api.InternalLogger
import com.openobserve.android.api.context.AccountInfo
import com.openobserve.android.api.context.OpenObserveContext
import com.openobserve.android.api.context.UserInfo
import com.openobserve.android.api.feature.Feature
import com.openobserve.android.api.feature.FeatureScope
import com.openobserve.android.api.feature.SdkFeatureMock
import com.openobserve.android.core.InternalSdkCore
import com.openobserve.android.log.LogAttributes
import com.openobserve.android.trace.api.span.OpenObserveSpan
import com.openobserve.android.trace.api.span.OpenObserveSpanBuilder
import com.openobserve.android.trace.internal.RumContextPropagator.Companion.OPENOBSERVE_INITIAL_CONTEXT
import com.openobserve.android.trace.internal.RumContextPropagator.Companion.ERROR_FUTURE_GET_FAILED
import com.openobserve.android.trace.internal.RumContextPropagator.Companion.INITIAL_OPENOBSERVE_CONTEXT_NOT_AVAILABLE_ERROR
import com.openobserve.android.trace.internal.RumContextPropagator.Companion.extractRumContext
import com.openobserve.android.trace.internal.RumContextPropagator.Companion.injectRumContext
import com.openobserve.android.trace.utils.RumContextTestsUtils.RUM_CONTEXT_ACTION_ID
import com.openobserve.android.trace.utils.RumContextTestsUtils.RUM_CONTEXT_APPLICATION_ID
import com.openobserve.android.trace.utils.RumContextTestsUtils.RUM_CONTEXT_SESSION_ID
import com.openobserve.android.trace.utils.RumContextTestsUtils.RUM_CONTEXT_VIEW_ID
import com.openobserve.android.trace.utils.RumContextTestsUtils.aOpenObserveContextWithRumContext
import com.openobserve.android.trace.utils.RumContextTestsUtils.aRumContext
import com.openobserve.android.utils.forge.Configurator
import com.openobserve.android.utils.verifyLog
import com.openobserve.tools.unit.completedFutureMock
import com.openobserve.tools.unit.completedWithErrorFutureMock
import com.openobserve.tools.unit.forge.anException
import com.openobserve.tools.unit.incompleteFutureMock
import com.openobserve.trace.core.DDSpan
import com.openobserve.trace.core.propagation.HttpCodec
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.annotation.Forgery
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
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.quality.Strictness
import org.mockito.verification.VerificationMode
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

@Extensions(
    ExtendWith(MockitoExtension::class),
    ExtendWith(ForgeExtension::class)
)
@MockitoSettings(strictness = Strictness.LENIENT)
@ForgeConfiguration(Configurator::class)
class RumContextPropagatorTest {

    private lateinit var mockRumFeatureScope: FeatureScope

    @Mock
    private lateinit var mockSpanBuilder: OpenObserveSpanBuilder

    @Mock
    private lateinit var mockInternalLogger: InternalLogger

    private lateinit var fakeUserInfo: UserInfo
    private var fakeAccountInfo: AccountInfo? = null

    @Forgery
    private lateinit var fakeOpenObserveContext: OpenObserveContext
    private lateinit var fakeRumContext: Map<String, Any?>

    private val mockSdkCore = mock<InternalSdkCore> {
        on { getFeature(Feature.RUM_FEATURE_NAME) } doAnswer { mockRumFeatureScope }
        on { internalLogger } doAnswer { mockInternalLogger }
    }

    private val testedRumContextPropagator = RumContextPropagator { mockSdkCore }

    @BeforeEach
    fun `set up`(forge: Forge) {
        fakeAccountInfo = forge.aNullable { AccountInfo(id = forge.aString()) }
        fakeUserInfo = UserInfo(id = forge.aString())
        mockRumFeatureScope = SdkFeatureMock.create()
        fakeRumContext = forge.aRumContext()
        fakeOpenObserveContext = forge.aOpenObserveContextWithRumContext(fakeRumContext, fakeAccountInfo, fakeUserInfo)
    }

    @Test
    fun `M get(Long, TimeUnit) W extractRumContext {DDSpan, block=True}`() {
        // Given
        val futureMock = incompleteFutureMock<OpenObserveContext>()
        val span = newDDSpanWithLazyOpenObserveContext(futureMock)

        // When
        span.extractRumContext(testedRumContextPropagator, block = true)

        // Then
        verify(futureMock).get(1, TimeUnit.SECONDS)
    }

    @Test
    fun `M get(Long, TimeUnit) W extractRumContext {OpenObserveSpan, block=True}`() {
        // Given
        val futureMock = incompleteFutureMock<OpenObserveContext>()
        val span = newOpenObserveSpanWithLazyOpenObserveContext(futureMock)

        // When
        span.extractRumContext(testedRumContextPropagator, block = true)

        // Then
        verify(futureMock).get(1, TimeUnit.SECONDS)
    }

    @Test
    fun `M not block W extractRumContext {DDSpan, isDone=true, block=False}`() {
        // Given
        val futureMock = completedFutureMock<OpenObserveContext?>(null)
        val span = newDDSpanWithLazyOpenObserveContext(futureMock)

        // When
        span.extractRumContext(mock(), block = false)

        // Then
        inOrder(futureMock) {
            verify(futureMock).isDone
            verify(futureMock).get()
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `M not block W extractRumContext {OpenObserveSpan, isDone=true, block=False}`() {
        // Given
        val futureMock = completedFutureMock<OpenObserveContext?>(null)
        val span = newOpenObserveSpanWithLazyOpenObserveContext(futureMock)

        // When
        span.extractRumContext(mock(), block = false)

        // Then
        inOrder(futureMock) {
            verify(futureMock).isDone
            verify(futureMock).get()
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `M not block W extractRumContext {DDSpan, isDone=false, block=False}`() {
        // Given
        val futureMock = incompleteFutureMock<OpenObserveContext>()
        val span = newDDSpanWithLazyOpenObserveContext(futureMock)

        // When
        span.extractRumContext(testedRumContextPropagator, block = false)

        // Then
        inOrder(futureMock) {
            verify(futureMock).isDone
            verify(futureMock, never()).get()
            verifyNoMoreInteractions()
        }
        mockInternalLogger.verifyErrorLogged(INITIAL_OPENOBSERVE_CONTEXT_NOT_AVAILABLE_ERROR)
        verifyRumContextNotExtracted(span)
    }

    @Test
    fun `M not block W extractRumContext {OpenObserveSpan, isDone=false, block=False}`() {
        // Given
        val futureMock = incompleteFutureMock<OpenObserveContext>()
        val span = newOpenObserveSpanWithLazyOpenObserveContext(futureMock)

        // When
        span.extractRumContext(testedRumContextPropagator, block = false)

        // Then
        inOrder(futureMock) {
            verify(futureMock).isDone
            verify(futureMock, never()).get()
            verifyNoMoreInteractions()
        }
        mockInternalLogger.verifyErrorLogged(INITIAL_OPENOBSERVE_CONTEXT_NOT_AVAILABLE_ERROR)
        verifyRumContextNotExtracted(span)
    }

    @Test
    fun `M log ERROR_FUTURE_GET_FAILED if Future#get(Long, TimeUnit) failed { DDSpan, block = true }`(forge: Forge) {
        // Given
        val span = newDDSpanWithLazyOpenObserveContext(
            completedWithErrorFutureMock(forge.anException())
        )

        // When
        span.extractRumContext(testedRumContextPropagator, block = true)

        // Then
        mockInternalLogger.verifyErrorLogged(ERROR_FUTURE_GET_FAILED, mode = atLeastOnce())
        verifyRumContextNotExtracted(span)
    }

    @Test
    fun `M log ERROR_FUTURE_GET_FAILED if Future#get(Long, TimeUnit) failed { OpenObserveSpan, block = true }`(
        forge: Forge
    ) {
        // Given
        val span = newOpenObserveSpanWithLazyOpenObserveContext(
            completedWithErrorFutureMock(forge.anException())
        )

        // When
        span.extractRumContext(testedRumContextPropagator, block = true)

        // Then
        mockInternalLogger.verifyErrorLogged(ERROR_FUTURE_GET_FAILED, mode = atLeastOnce())
        verifyRumContextNotExtracted(span)
    }

    @Test
    fun `M log ERROR_FUTURE_GET_FAILED if Future#get() failed { DDSpan, block = false }`(forge: Forge) {
        // Given
        val span = newOpenObserveSpanWithLazyOpenObserveContext(
            completedWithErrorFutureMock(forge.anException())
        )

        // When
        span.extractRumContext(testedRumContextPropagator, block = false)

        // Then
        mockInternalLogger.verifyErrorLogged(ERROR_FUTURE_GET_FAILED, mode = atLeastOnce())
        verifyRumContextNotExtracted(span)
    }

    @Test
    fun `M log ERROR_FUTURE_GET_FAILED if Future#get() failed { OpenObserveSpan, block = false }`(forge: Forge) {
        // Given
        val span = newOpenObserveSpanWithLazyOpenObserveContext(
            completedWithErrorFutureMock(forge.anException())
        )

        // When
        span.extractRumContext(testedRumContextPropagator, block = false)

        // Then
        mockInternalLogger.verifyErrorLogged(ERROR_FUTURE_GET_FAILED, mode = atLeastOnce())
    }

    @Test
    fun `M withTag(OPENOBSERVE_INITIAL_CONTEXT) W injectRumContextFeature`() {
        // Given
        val futureMock = completedFutureMock(fakeOpenObserveContext)
        mockRumFeatureScope = SdkFeatureMock.create(futureMock)

        // When
        mockSpanBuilder.injectRumContext(testedRumContextPropagator)

        // Then
        argumentCaptor<Future<OpenObserveContext>> {
            verify(mockSpanBuilder).withTag(eq(OPENOBSERVE_INITIAL_CONTEXT), capture())
            assertThat(firstValue).isEqualTo(futureMock)
        }
    }

    @Test
    fun `M set RUM tags W extractRumContext { OpenObserveSpan }`() {
        // Given
        val span = newOpenObserveSpanWithLazyOpenObserveContext(
            completedFutureMock(fakeOpenObserveContext)
        )

        // When
        span.extractRumContext(testedRumContextPropagator)

        // Then
        verify(span).getTag(OPENOBSERVE_INITIAL_CONTEXT)
        verify(span).setTag(LogAttributes.RUM_APPLICATION_ID, fakeRumContext[RUM_CONTEXT_APPLICATION_ID])
        verify(span).setTag(LogAttributes.RUM_SESSION_ID, fakeRumContext[RUM_CONTEXT_SESSION_ID])
        verify(span).setTag(LogAttributes.RUM_VIEW_ID, fakeRumContext[RUM_CONTEXT_VIEW_ID])
        verify(span).setTag(LogAttributes.RUM_ACTION_ID, fakeRumContext[RUM_CONTEXT_ACTION_ID])
        verify(span).setTag(HttpCodec.RUM_KEY_ACCOUNT_ID, fakeOpenObserveContext.accountInfo?.id as? Any)
        verify(span).setTag(HttpCodec.RUM_KEY_USER_ID, fakeOpenObserveContext.userInfo.id as? Any)
        verify(span).setTag(OPENOBSERVE_INITIAL_CONTEXT, null as Any?)

        verifyNoMoreInteractions(span)
        verifyNoInteractions(mockInternalLogger)
    }

    @Test
    fun `M set RUM tags W extractRumContext { DDSpan }`() {
        // Given
        val span = newDDSpanWithLazyOpenObserveContext(
            completedFutureMock(fakeOpenObserveContext)
        )

        // When
        span.extractRumContext(testedRumContextPropagator)

        // Then
        verify(span).getTag(OPENOBSERVE_INITIAL_CONTEXT)
        verify(span).setTag(LogAttributes.RUM_APPLICATION_ID, fakeRumContext[RUM_CONTEXT_APPLICATION_ID])
        verify(span).setTag(LogAttributes.RUM_SESSION_ID, fakeRumContext[RUM_CONTEXT_SESSION_ID])
        verify(span).setTag(LogAttributes.RUM_VIEW_ID, fakeRumContext[RUM_CONTEXT_VIEW_ID])
        verify(span).setTag(LogAttributes.RUM_ACTION_ID, fakeRumContext[RUM_CONTEXT_ACTION_ID])
        verify(span).setTag(HttpCodec.RUM_KEY_ACCOUNT_ID, fakeOpenObserveContext.accountInfo?.id as? Any)
        verify(span).setTag(HttpCodec.RUM_KEY_USER_ID, fakeOpenObserveContext.userInfo.id as? Any)
        verify(span).setTag(OPENOBSERVE_INITIAL_CONTEXT, null as Any?)

        verifyNoMoreInteractions(span)
        verifyNoInteractions(mockInternalLogger)
    }

    @Test
    fun `M set null RUM tags W extractRumContext { OpenObserveSpan, rum context is empty }`(forge: Forge) {
        // Given
        val futureMock = completedFutureMock(forge.aOpenObserveContextWithRumContext(emptyMap()))
        val span = newOpenObserveSpanWithLazyOpenObserveContext(futureMock)
        // When
        span.extractRumContext(testedRumContextPropagator)

        // Then
        verify(span).getTag(OPENOBSERVE_INITIAL_CONTEXT)
        verify(span).setTag(LogAttributes.RUM_APPLICATION_ID, null as Any?)
        verify(span).setTag(LogAttributes.RUM_SESSION_ID, null as Any?)
        verify(span).setTag(LogAttributes.RUM_VIEW_ID, null as Any?)
        verify(span).setTag(LogAttributes.RUM_ACTION_ID, null as Any?)
        verify(span).setTag(HttpCodec.RUM_KEY_ACCOUNT_ID, null as Any?)
        verify(span).setTag(HttpCodec.RUM_KEY_USER_ID, null as Any?)
        verify(span).setTag(OPENOBSERVE_INITIAL_CONTEXT, null as Any?)

        verifyNoMoreInteractions(span)
        verifyNoInteractions(mockInternalLogger)
    }

    @Test
    fun `M set null RUM tags W extractRumContext { DDSpan, rum context is empty }`(forge: Forge) {
        // Given
        val futureMock = completedFutureMock(forge.aOpenObserveContextWithRumContext(emptyMap()))
        val span = newDDSpanWithLazyOpenObserveContext(futureMock)

        // When
        span.extractRumContext(testedRumContextPropagator)

        // Then
        verify(span).getTag(OPENOBSERVE_INITIAL_CONTEXT)
        verify(span).setTag(LogAttributes.RUM_APPLICATION_ID, null as Any?)
        verify(span).setTag(LogAttributes.RUM_SESSION_ID, null as Any?)
        verify(span).setTag(LogAttributes.RUM_VIEW_ID, null as Any?)
        verify(span).setTag(LogAttributes.RUM_ACTION_ID, null as Any?)
        verify(span).setTag(HttpCodec.RUM_KEY_ACCOUNT_ID, null as Any?)
        verify(span).setTag(HttpCodec.RUM_KEY_USER_ID, null as Any?)
        verify(span).setTag(OPENOBSERVE_INITIAL_CONTEXT, null as Any?)

        verifyNoMoreInteractions(span)
        verifyNoInteractions(mockInternalLogger)
    }

    @Test
    fun `M not call setTag W getTag(OPENOBSERVE_INITIAL_CONTEXT) is null { OpenObserveSpan }`() {
        // Given
        val span = newOpenObserveSpanWithLazyOpenObserveContext(null)

        // When
        span.extractRumContext(testedRumContextPropagator)

        // Then
        verify(span).getTag(OPENOBSERVE_INITIAL_CONTEXT)
        verifyNoMoreInteractions(span)
        verifyNoInteractions(mockInternalLogger)
    }

    @Test
    fun `M not call setTag W getTag(OPENOBSERVE_INITIAL_CONTEXT) is null { DDSpan }`() {
        // Given
        val span = newDDSpanWithLazyOpenObserveContext(null)

        // When
        span.extractRumContext(testedRumContextPropagator)

        // Then
        verify(span).getTag(OPENOBSERVE_INITIAL_CONTEXT)
        verifyNoMoreInteractions(span)
        verifyNoInteractions(mockInternalLogger)
    }

    companion object {
        fun newDDSpanWithLazyOpenObserveContext(value: Future<OpenObserveContext?>?) = mock<DDSpan> {
            on { getTag(OPENOBSERVE_INITIAL_CONTEXT) } doAnswer { value }
        }

        fun newOpenObserveSpanWithLazyOpenObserveContext(value: Future<OpenObserveContext?>?) =
            mock<OpenObserveSpan> {
                on { getTag(OPENOBSERVE_INITIAL_CONTEXT) } doAnswer { value }
            }

        fun InternalLogger.verifyErrorLogged(message: String, mode: VerificationMode = times(1)) = verifyLog(
            InternalLogger.Level.ERROR,
            InternalLogger.Target.MAINTAINER,
            message,
            mode = mode
        )

        fun verifyRumContextNotExtracted(span: Any) {
            when (span) {
                is DDSpan -> {
                    verify(span).getTag(OPENOBSERVE_INITIAL_CONTEXT)
                    verify(span).setTag(OPENOBSERVE_INITIAL_CONTEXT, null as Any?)
                }

                is OpenObserveSpan -> {
                    verify(span).getTag(OPENOBSERVE_INITIAL_CONTEXT)
                    verify(span).setTag(OPENOBSERVE_INITIAL_CONTEXT, null as Any?)
                }
            }
            verifyNoMoreInteractions(span)
        }
    }
}
