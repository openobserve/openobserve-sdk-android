/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.trace.internal.domain.event

import com.openobserve.android.api.context.OpenObserveContext
import com.openobserve.android.log.LogAttributes
import com.openobserve.android.trace.assertj.SpanEventAssert.Companion.assertThat
import com.openobserve.android.utils.forge.Configurator
import com.openobserve.trace.api.DDSpanId
import com.openobserve.trace.api.internal.util.LongStringUtils
import com.openobserve.trace.core.DDSpan
import com.openobserve.trace.core.DDSpanContext
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.annotation.BoolForgery
import fr.xgouchet.elmyr.annotation.Forgery
import fr.xgouchet.elmyr.annotation.StringForgery
import fr.xgouchet.elmyr.junit5.ForgeConfiguration
import fr.xgouchet.elmyr.junit5.ForgeExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness

@Extensions(
    ExtendWith(MockitoExtension::class),
    ExtendWith(ForgeExtension::class)
)
@MockitoSettings(strictness = Strictness.LENIENT)
@ForgeConfiguration(Configurator::class)
internal class CoreTracerSpanToSpanEventMapperTest {

    private lateinit var testedMapper: CoreTracerSpanToSpanEventMapper

    @Forgery
    lateinit var fakeOpenObserveContext: OpenObserveContext

    @BoolForgery
    var fakeNetworkInfoEnabled: Boolean = false

    // region Tests

    @BeforeEach
    fun `set up`() {
        testedMapper = CoreTracerSpanToSpanEventMapper(fakeNetworkInfoEnabled)
    }

    @Test
    fun `M map a DdSpan to a SpanEvent W map()`(
        @Forgery fakeSpan: DDSpan
    ) {
        // Given
        val expectedMeta = fakeSpan.baggage + fakeSpan.tags.map {
            it.key to it.value.toString()
        }
        val expectedMetrics = fakeSpan.expectedMetrics()

        // When
        val event = testedMapper.map(fakeOpenObserveContext, fakeSpan)

        // Then
        assertThat(event)
            .hasSpanId(DDSpanId.toHexStringPadded(fakeSpan.spanId))
            .hasLeastSignificant64BitsTraceId(LongStringUtils.toHexStringPadded(fakeSpan.traceId.toLong(), 16))
            .hasMostSignificant64BitsTraceId(LongStringUtils.toHexStringPadded(fakeSpan.traceId.toHighOrderLong(), 16))
            .hasParentId(DDSpanId.toHexStringPadded(fakeSpan.parentId))
            .hasServiceName(fakeSpan.serviceName)
            .hasOperationName(fakeSpan.operationName.toString())
            .hasResourceName(fakeSpan.resourceName.toString())
            .hasSpanType("custom")
            .hasSpanSource(fakeOpenObserveContext.source)
            .hasApplicationId(null)
            .hasSessionId(null)
            .hasViewId(null)
            .hasErrorFlag(fakeSpan.error.toLong())
            .hasSpanStartTime(fakeSpan.startTime + fakeOpenObserveContext.time.serverTimeOffsetNs)
            .hasSpanDuration(fakeSpan.durationNano)
            .hasSpanLinks(fakeSpan.links)
            .hasTracerVersion(fakeOpenObserveContext.sdkVersion)
            .hasClientPackageVersion(fakeOpenObserveContext.version).apply {
                if (fakeNetworkInfoEnabled) {
                    hasNetworkInfo(fakeOpenObserveContext.networkInfo)
                } else {
                    doesntHaveNetworkInfo()
                }
            }
            .hasDeviceInfo(fakeOpenObserveContext.deviceInfo)
            .hasOsInfo(fakeOpenObserveContext.deviceInfo)
            .hasUserInfo(fakeOpenObserveContext.userInfo)
            .hasAccountInfo(fakeOpenObserveContext.accountInfo)
            .hasVariant(fakeOpenObserveContext.variant)
            .hasMeta(expectedMeta)
            .hasMetrics(expectedMetrics)
    }

    @Test
    fun `M map a DdSpan to a SpanEvent with RUM info W map() {RUM info present}`(
        @Forgery fakeSpan: DDSpan,
        @StringForgery fakeApplicationId: String,
        @StringForgery fakeSessionId: String,
        @StringForgery fakeViewId: String
    ) {
        // Given
        val tags = fakeSpan.tags.toMutableMap().apply {
            this[LogAttributes.RUM_APPLICATION_ID] = fakeApplicationId
            this[LogAttributes.RUM_SESSION_ID] = fakeSessionId
            this[LogAttributes.RUM_VIEW_ID] = fakeViewId
        }
        whenever(fakeSpan.tags).thenReturn(tags)
        whenever(fakeSpan.context().tags).thenReturn(tags)

        // Given
        val expectedMeta = fakeSpan.baggage + fakeSpan.tags.map {
            it.key to it.value.toString()
        }
        val expectedMetrics = fakeSpan.expectedMetrics()

        // When
        val event = testedMapper.map(fakeOpenObserveContext, fakeSpan)

        // Then
        assertThat(event)
            .hasSpanId(DDSpanId.toHexStringPadded(fakeSpan.spanId))
            .hasLeastSignificant64BitsTraceId(LongStringUtils.toHexStringPadded(fakeSpan.traceId.toLong(), 16))
            .hasMostSignificant64BitsTraceId(LongStringUtils.toHexStringPadded(fakeSpan.traceId.toHighOrderLong(), 16))
            .hasParentId(DDSpanId.toHexStringPadded(fakeSpan.parentId))
            .hasServiceName(fakeSpan.serviceName)
            .hasOperationName(fakeSpan.operationName.toString())
            .hasResourceName(fakeSpan.resourceName.toString())
            .hasSpanType("custom")
            .hasSpanSource(fakeOpenObserveContext.source)
            .hasApplicationId(fakeApplicationId)
            .hasSessionId(fakeSessionId)
            .hasViewId(fakeViewId)
            .hasErrorFlag(fakeSpan.error.toLong())
            .hasSpanStartTime(fakeSpan.startTime + fakeOpenObserveContext.time.serverTimeOffsetNs)
            .hasSpanDuration(fakeSpan.durationNano)
            .hasSpanLinks(fakeSpan.links)
            .hasTracerVersion(fakeOpenObserveContext.sdkVersion)
            .hasClientPackageVersion(fakeOpenObserveContext.version).apply {
                if (fakeNetworkInfoEnabled) {
                    hasNetworkInfo(fakeOpenObserveContext.networkInfo)
                } else {
                    doesntHaveNetworkInfo()
                }
            }.hasUserInfo(fakeOpenObserveContext.userInfo)
            .hasAccountInfo(fakeOpenObserveContext.accountInfo)
            .hasDeviceInfo(fakeOpenObserveContext.deviceInfo)
            .hasOsInfo(fakeOpenObserveContext.deviceInfo)
            .hasVariant(fakeOpenObserveContext.variant)
            .hasMeta(expectedMeta)
            .hasMetrics(expectedMetrics)
    }

    @Test
    fun `M mark the SpanEvent as top span W map() { parentId is 0 }`(
        @Forgery fakeSpan: DDSpan
    ) {
        // Given
        whenever(fakeSpan.parentId).thenReturn(0L)

        // When
        val event = testedMapper.map(fakeOpenObserveContext, fakeSpan)

        // Then
        assertThat(event).isTopSpan()
    }

    @Test
    fun `M not mark the SpanEvent as top span W map() { parentId is different than 0 }`(
        forge: Forge,
        @Forgery fakeSpan: DDSpan
    ) {
        // Given
        whenever(fakeSpan.parentId).thenReturn(forge.aLong(min = 1))

        // When
        val event = testedMapper.map(fakeOpenObserveContext, fakeSpan)

        // Then
        assertThat(event).isNotTopSpan()
    }

    // endregion

    // region Internal

    private fun DDSpan.expectedMetrics(): Map<String, Number> {
        return tags.filterValues { it is Number }.mapValues { it.value as Number }.toMutableMap().apply {
            this[DDSpanContext.PRIORITY_SAMPLING_KEY] = spanSamplingPriority
        }
    }

    // endregion
}
