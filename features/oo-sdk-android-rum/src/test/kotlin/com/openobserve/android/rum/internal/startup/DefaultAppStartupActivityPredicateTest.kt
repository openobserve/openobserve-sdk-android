/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.internal.startup

import android.app.Activity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock

internal class DefaultAppStartupActivityPredicateTest {

    @Test
    fun `M return true W shouldTrackStartup()`() {
        // Given
        val activity = mock<Activity>()

        // When
        val result = DefaultAppStartupActivityPredicate.shouldTrackStartup(activity)

        // Then
        assertThat(result).isTrue()
    }
}
