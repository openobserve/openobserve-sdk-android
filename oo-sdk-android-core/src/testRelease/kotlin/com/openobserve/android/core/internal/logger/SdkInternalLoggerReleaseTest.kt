/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.internal.logger

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock

internal class SdkInternalLoggerReleaseTest {

    // region sdkLogger

    @Test
    @Suppress("FunctionNaming", "FunctionMaxLength")
    fun `M not build sdkLogger W init()`() {
        // When
        val logger = SdkInternalLogger(
            sdkCore = mock()
        )

        // Then
        assertThat(logger.maintainerLogger).isNull()
    }

    // endregion
}
