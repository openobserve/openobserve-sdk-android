/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.utils.assertj

import com.openobserve.android.internal.telemetry.InternalTelemetryEvent
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Assertions

internal class InternalApiUsageNetworkInstrumentationEventAssert(
    actual: InternalTelemetryEvent.ApiUsage.NetworkInstrumentation
) : AbstractAssert<
    InternalApiUsageNetworkInstrumentationEventAssert,
    InternalTelemetryEvent.ApiUsage.NetworkInstrumentation
    >(
    actual,
    InternalApiUsageNetworkInstrumentationEventAssert::class.java
) {

    fun isEqualTo(expected: InternalTelemetryEvent.ApiUsage.NetworkInstrumentation) {
        hasType(expected.type)
        hasAdditionalProperties(expected.additionalProperties)
    }

    private fun hasType(expected: InternalTelemetryEvent.ApiUsage.NetworkInstrumentation.LibraryType) = apply {
        Assertions.assertThat(actual.type)
            .overridingErrorMessage(
                "Expected NetworkInstrumentation event to have" +
                    " type $expected but was ${actual.type}"
            )
            .isEqualTo(expected)
    }

    private fun hasAdditionalProperties(expected: Map<String, Any?>) = apply {
        Assertions.assertThat(actual.additionalProperties)
            .overridingErrorMessage(
                "Expected NetworkInstrumentation event to have" +
                    " additionalProperties $expected but was ${actual.additionalProperties}"
            )
            .isEqualTo(expected)
    }

    companion object {
        fun assertThat(actual: InternalTelemetryEvent.ApiUsage.NetworkInstrumentation) =
            InternalApiUsageNetworkInstrumentationEventAssert(actual)
    }
}
