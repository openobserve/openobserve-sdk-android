/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.trace.internal

import com.openobserve.trace.api.DDSpanId.toHexStringPadded
import fr.xgouchet.elmyr.annotation.LongForgery
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OpenObserveSpanIdConverterAdapterTest {

    @LongForgery
    private var fakeLong: Long = 0L

    @Test
    fun `M serialize to String and deserialize valid W fromHex(toHexStringPadded(Long))`() {
        // Given
        val converter = OpenObserveSpanIdConverter()

        // When
        val actual = converter.fromHex(toHexStringPadded(fakeLong))

        // Then
        assertThat(actual).isEqualTo(fakeLong)
    }
}
