/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.utils.assertj

import com.openobserve.android.internal.telemetry.InternalTelemetryEvent
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Assertions.assertThat

class InternalAddOperationStepVitalAssert(actual: InternalTelemetryEvent.ApiUsage.AddOperationStepVital) :
    AbstractAssert<InternalAddOperationStepVitalAssert, InternalTelemetryEvent.ApiUsage.AddOperationStepVital>(
        actual,
        InternalAddOperationStepVitalAssert::class.java
    ) {

    fun isEqualTo(expected: InternalTelemetryEvent.ApiUsage.AddOperationStepVital) {
        hasActionType(expected.actionType)
    }

    private fun hasActionType(
        expected: InternalTelemetryEvent.ApiUsage.AddOperationStepVital.ActionType
    ): InternalAddOperationStepVitalAssert {
        assertThat(actual.actionType)
            .overridingErrorMessage(
                "Expected operationStepVital event to" +
                    " have actionType $expected but was ${actual.actionType}"
            )
            .isEqualTo(expected)
        return this
    }

    companion object {
        fun assertThat(actual: InternalTelemetryEvent.ApiUsage.AddOperationStepVital) =
            InternalAddOperationStepVitalAssert(actual)
    }
}
