/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.internal.net

import org.assertj.core.api.Assertions.assertThat
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.verification.VerificationMode

fun RumNetworkInstrumentation.verifyReportInstrumentationError(
    message: String,
    mode: VerificationMode = times(1)
) {
    argumentCaptor<() -> String> {
        verify(this@verifyReportInstrumentationError, mode).reportInstrumentationError(capture())
        allValues.forEach { assertThat(it()).isEqualTo(message) }
    }
}
