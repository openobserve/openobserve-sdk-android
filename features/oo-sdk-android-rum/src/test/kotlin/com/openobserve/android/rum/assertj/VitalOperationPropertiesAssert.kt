/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.assertj

import com.openobserve.android.rum.internal.domain.scope.toSchemaFailureReason
import com.openobserve.android.rum.model.VitalOperationStepEvent
import com.openobserve.android.rum.operations.FailureReason
import org.assertj.core.api.AbstractObjectAssert
import org.assertj.core.api.Assertions.assertThat

internal class VitalOperationPropertiesAssert(actual: VitalOperationStepEvent.Vital) :
    AbstractObjectAssert<VitalOperationPropertiesAssert, VitalOperationStepEvent.Vital>(
        actual,
        VitalOperationPropertiesAssert::class.java
    ) {

    fun hasVitalName(expected: String) = apply {
        assertThat(actual.name)
            .overridingErrorMessage(
                "Expected event data to have vital.name $expected but was ${actual.name}"
            )
            .isEqualTo(expected)
    }

    fun hasVitalOperationalKey(expected: String?) = apply {
        assertThat(actual.operationKey)
            .overridingErrorMessage(
                "Expected event data to have vital.operationKey $expected but was ${actual.operationKey}"
            )
            .isEqualTo(expected)
    }

    fun hasVitalStepType(expected: VitalOperationStepEvent.StepType) = apply {
        assertThat(actual.stepType)
            .overridingErrorMessage(
                "Expected event data to have vital.operationKey $expected but was ${actual.stepType}"
            )
            .isEqualTo(expected)
    }

    fun hasVitalFailureReason(expected: FailureReason) = apply {
        assertThat(actual.failureReason)
            .overridingErrorMessage(
                "Expected event data to have vital.failureReason $expected but was ${actual.failureReason}"
            )
            .isEqualTo(expected.toSchemaFailureReason())
    }

    fun hasNoVitalFailureReason() = apply {
        assertThat(actual.failureReason)
            .overridingErrorMessage(
                "Expected event data to have vital.failureReason to be null but was ${actual.failureReason}"
            )
            .isNull()
    }

    companion object {
        internal fun assertThat(
            actual: VitalOperationStepEvent.Vital
        ): VitalOperationPropertiesAssert = VitalOperationPropertiesAssert(actual)
    }
}
