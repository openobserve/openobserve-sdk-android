/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.utils.assertj

import com.openobserve.android.internal.telemetry.InternalTelemetryEvent
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Assertions.assertThat

class InternalTrackWebViewEventAssert(actual: InternalTelemetryEvent.ApiUsage.TrackWebView) :
    AbstractAssert<InternalTrackWebViewEventAssert, InternalTelemetryEvent.ApiUsage.TrackWebView>(
        actual,
        InternalTrackWebViewEventAssert::class.java
    ) {

    fun isEqualTo(expected: InternalTelemetryEvent.ApiUsage.TrackWebView) {
        hasAdditionalProperties(expected.additionalProperties)
    }

    fun hasAdditionalProperties(expected: Map<String, Any?>): InternalTrackWebViewEventAssert {
        assertThat(actual.additionalProperties)
            .overridingErrorMessage(
                "Expected trackWebView event to have" +
                    " additionalProperties $expected but was ${actual.additionalProperties}"
            )
            .isEqualTo(expected)
        return this
    }

    companion object {
        fun assertThat(actual: InternalTelemetryEvent.ApiUsage.TrackWebView) =
            InternalTrackWebViewEventAssert(actual)
    }
}
