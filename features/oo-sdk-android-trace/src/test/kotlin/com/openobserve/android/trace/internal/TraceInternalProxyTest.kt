/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.trace.internal

import com.openobserve.android.trace.ApmNetworkInstrumentationConfiguration
import com.openobserve.android.trace.TracingHeaderType
import com.openobserve.android.utils.forge.Configurator
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.annotation.StringForgery
import fr.xgouchet.elmyr.junit5.ForgeConfiguration
import fr.xgouchet.elmyr.junit5.ForgeExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
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
@ForgeConfiguration(Configurator::class)
internal class TraceInternalProxyTest {

    private lateinit var fakeTracedHosts: Map<String, Set<TracingHeaderType>>

    @StringForgery
    lateinit var fakeInstrumentationName: String

    @BeforeEach
    fun `set up`(forge: Forge) {
        fakeTracedHosts = forge.aMap {
            aStringMatching("[a-z]+\\.[a-z]{2,3}") to forge.aList {
                aValueFrom(TracingHeaderType::class.java)
            }.toSet()
        }
    }

    @Test
    fun `M build TracingInstrumentation W createApmNetworkInstrumentation()`() {
        // Given
        val builder = ApmNetworkInstrumentationConfiguration(fakeTracedHosts)

        // When
        val result: ApmNetworkInstrumentation = _TraceInternalProxy.createApmNetworkInstrumentation(
            fakeInstrumentationName,
            builder
        )

        // Then
        assertThat(result).isNotNull
        assertThat(result.sdkInstanceName).isNull()
        assertThat(result.traceOrigin).isNull()
    }

    @Test
    fun `M build TracingInstrumentation with configured values W createApmNetworkInstrumentation()`(
        @StringForgery fakeSdkInstanceName: String,
        @StringForgery fakeTraceOrigin: String
    ) {
        // Given
        val builder = ApmNetworkInstrumentationConfiguration(fakeTracedHosts)
            .setSdkInstanceName(fakeSdkInstanceName)
            .setTraceOrigin(fakeTraceOrigin)

        // When
        val result: ApmNetworkInstrumentation =
            _TraceInternalProxy.createApmNetworkInstrumentation(
                fakeInstrumentationName,
                builder
            )

        // Then
        assertThat(result).isNotNull
        assertThat(result.sdkInstanceName).isEqualTo(fakeSdkInstanceName)
        assertThat(result.traceOrigin).isEqualTo(fakeTraceOrigin)
    }
}
