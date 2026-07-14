/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.trace.internal

import android.util.Log
import com.openobserve.android.api.feature.Feature
import com.openobserve.android.api.feature.FeatureScope
import com.openobserve.android.api.feature.FeatureSdkCore
import com.openobserve.android.internal.time.TimeProvider
import com.openobserve.android.internal.utils.loggableStackTrace
import com.openobserve.android.log.LogAttributes
import com.openobserve.android.trace.api.OpenObserveTracingConstants
import com.openobserve.android.trace.api.span.OpenObserveSpan
import com.openobserve.android.trace.internal.OpenObserveSpanLogger.Companion.DEFAULT_EVENT_MESSAGE
import com.openobserve.android.trace.internal.OpenObserveSpanLogger.Companion.TRACE_LOGGER_NAME
import com.openobserve.android.utils.forge.Configurator
import fr.xgouchet.elmyr.Forge
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
class OpenObserveSpanLoggerTest {

    private lateinit var mockSdkCore: FeatureSdkCore

    @StringForgery
    lateinit var fakeString: String

    @Forgery
    lateinit var fakeThrowable: Throwable

    @Mock
    lateinit var mockSpan: OpenObserveSpan

    @Forgery
    lateinit var fakeSpan: OpenObserveSpan

    @LongForgery(min = 0L)
    var fakeTimestamp: Long = 0L

    @Mock
    lateinit var mockLogFeatureScope: FeatureScope

    @Mock
    lateinit var mockTimeProvider: TimeProvider

    private lateinit var testedLogger: OpenObserveSpanLogger

    @BeforeEach
    fun `set up`() {
        mockSdkCore = mock<FeatureSdkCore> {
            on { getFeature(Feature.LOGS_FEATURE_NAME) } doReturn mockLogFeatureScope
        }

        whenever(mockSdkCore.timeProvider) doReturn mockTimeProvider
        whenever(mockTimeProvider.getDeviceTimestampMillis()) doReturn fakeTimestamp

        testedLogger = OpenObserveSpanLogger(mockSdkCore)
    }

    @Test
    fun `M send expected event W log(String)`() {
        // When
        testedLogger.log(fakeString, fakeSpan)

        // Then
        argumentCaptor<Map<Any, Any>> {
            verify(mockLogFeatureScope).sendEvent(capture())

            assertThat(firstValue["type"]).isEqualTo("span_log")
            assertThat(firstValue["loggerName"]).isEqualTo(TRACE_LOGGER_NAME)
            assertThat(firstValue["message"]).isEqualTo(DEFAULT_EVENT_MESSAGE)
            assertThat(firstValue["logStatus"]).isEqualTo(Log.VERBOSE)

            val attributes = firstValue["attributes"] as Map<*, *>
            assertThat(attributes[OpenObserveTracingConstants.LogAttributes.EVENT]).isEqualTo(fakeString)
            assertThat(attributes[LogAttributes.DD_SPAN_ID]).isEqualTo(fakeSpan.context().spanId.toString())
            assertThat(attributes[LogAttributes.DD_TRACE_ID]).isEqualTo(fakeSpan.context().traceId.toHexString())
        }
    }

    @Test
    fun `M send expected event W logErrorMessage(String)`() {
        // When
        testedLogger.logErrorMessage(fakeString, fakeSpan)

        // Then
        argumentCaptor<Map<Any, Any>> {
            verify(mockLogFeatureScope).sendEvent(capture())

            assertThat(firstValue["type"]).isEqualTo("span_log")
            assertThat(firstValue["loggerName"]).isEqualTo(TRACE_LOGGER_NAME)
            assertThat(firstValue["message"]).isEqualTo(fakeString)
            assertThat(firstValue["logStatus"]).isEqualTo(Log.ERROR)

            val attributes = firstValue["attributes"] as Map<*, *>
            assertThat(attributes[LogAttributes.DD_SPAN_ID]).isEqualTo(fakeSpan.context().spanId.toString())
            assertThat(attributes[LogAttributes.DD_TRACE_ID]).isEqualTo(fakeSpan.context().traceId.toHexString())
        }
    }

    @Test
    fun `M set expected tags W log(fakeThrowable)`() {
        // When
        testedLogger.log(fakeThrowable, mockSpan)

        // Then
        verify(mockSpan).isError = true
        verify(mockSpan).setTag(OpenObserveTracingConstants.Tags.KEY_ERROR_TYPE, fakeThrowable.javaClass.name)
        verify(mockSpan).setTag(OpenObserveTracingConstants.Tags.KEY_ERROR_MSG, fakeThrowable.message)
        verify(mockSpan).setTag(OpenObserveTracingConstants.Tags.KEY_ERROR_STACK, fakeThrowable.loggableStackTrace())
    }

    @Test
    fun `M send expected event W logErrorMessage(Map)`(forge: Forge) {
        // Given
        val fakeAttributes = forge.aMap { aString() to aString() }

        // When
        testedLogger.log(fakeAttributes, fakeSpan)

        // Then
        argumentCaptor<Map<Any, Any>> {
            verify(mockLogFeatureScope).sendEvent(capture())

            assertThat(firstValue["type"]).isEqualTo("span_log")
            assertThat(firstValue["loggerName"]).isEqualTo(TRACE_LOGGER_NAME)
            assertThat(firstValue["message"]).isEqualTo(DEFAULT_EVENT_MESSAGE)
            assertThat(firstValue["logStatus"]).isEqualTo(Log.VERBOSE)

            val attributes = (firstValue["attributes"] as Map<*, *>).toMutableMap()
            assertThat(attributes[LogAttributes.DD_SPAN_ID]).isEqualTo(fakeSpan.context().spanId.toString())
            assertThat(attributes[LogAttributes.DD_TRACE_ID]).isEqualTo(fakeSpan.context().traceId.toHexString())
            assertThat(attributes).containsAllEntriesOf(fakeAttributes)
        }
    }
}
