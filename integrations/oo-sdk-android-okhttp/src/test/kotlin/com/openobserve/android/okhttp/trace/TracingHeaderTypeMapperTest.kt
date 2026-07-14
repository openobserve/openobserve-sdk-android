/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.okhttp.trace

import com.openobserve.android.okhttp.internal.trace.toTelemetryTracingHeaderType
import com.openobserve.android.okhttp.internal.utils.forge.OkHttpConfigurator
import com.openobserve.android.trace.TracingHeaderType
import fr.xgouchet.elmyr.annotation.Forgery
import fr.xgouchet.elmyr.junit5.ForgeConfiguration
import fr.xgouchet.elmyr.junit5.ForgeExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.quality.Strictness

@Extensions(
    ExtendWith(MockitoExtension::class),
    ExtendWith(ForgeExtension::class)
)
@MockitoSettings(strictness = Strictness.LENIENT)
@ForgeConfiguration(OkHttpConfigurator::class)
internal class TracingHeaderTypeMapperTest {

    @Test
    fun `M map TracingHeaderType W toInternalTracingHeaderType`(
        @Forgery fakeTrackingHeaderType: TracingHeaderType
    ) {
        // When
        val result = fakeTrackingHeaderType.toTelemetryTracingHeaderType()

        // Then
        assertThat(result.name)
            .isEqualTo(fakeTrackingHeaderType.name)
    }
}
