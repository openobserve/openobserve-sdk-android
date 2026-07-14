/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.compose

import com.openobserve.tools.unit.ObjectTest
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.junit5.ForgeExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock

@ExtendWith(ForgeExtension::class)
internal class AcceptAllNavKeyPredicateTest : ObjectTest<AcceptAllNavKeyPredicate<Any>>() {

    override fun createInstance(forge: Forge): AcceptAllNavKeyPredicate<Any> {
        return AcceptAllNavKeyPredicate()
    }

    override fun createEqualInstance(
        source: AcceptAllNavKeyPredicate<Any>,
        forge: Forge
    ): AcceptAllNavKeyPredicate<Any> {
        return AcceptAllNavKeyPredicate()
    }

    override fun createUnequalInstance(
        source: AcceptAllNavKeyPredicate<Any>,
        forge: Forge
    ): AcceptAllNavKeyPredicate<Any>? {
        // Based on the equals implementation, any other class would be unequal.
        // Returning null as per the pattern in AcceptAllNavDestinationsTest
        // if there's no simple way to create an unequal instance of the same class.
        return null
    }

    @Test
    fun `M return true W accept()`() {
        // Given
        val fakeComponent: Any = mock()
        val testedPredicate = AcceptAllNavKeyPredicate<Any>()

        // When
        val result = testedPredicate.accept(fakeComponent)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `M return null W getViewName()`() {
        // Given
        val fakeComponent: Any = mock()
        val testedPredicate = AcceptAllNavKeyPredicate<Any>()

        // When
        val result = testedPredicate.getViewName(fakeComponent)

        // Then
        assertThat(result).isNull()
    }
}
