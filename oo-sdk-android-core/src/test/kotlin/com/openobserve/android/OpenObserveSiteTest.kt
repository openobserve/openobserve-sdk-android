/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android

import com.openobserve.android.utils.forge.Configurator
import fr.xgouchet.elmyr.junit5.ForgeConfiguration
import fr.xgouchet.elmyr.junit5.ForgeExtension
import org.assertj.core.api.Assertions.assertThat
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
internal class OpenObserveSiteTest {

    @Test
    fun `M return intake endpoint W intakeEndpoint {US1}`() {
        assertThat(OpenObserveSite.US1.intakeEndpoint).isEqualTo("https://browser-intake-openobserve.ai")
    }

    @Test
    fun `M return intake endpoint W intakeEndpoint {US3}`() {
        assertThat(OpenObserveSite.US3.intakeEndpoint).isEqualTo("https://browser-intake-us3-openobserve.ai")
    }

    @Test
    fun `M return intake endpoint W intakeEndpoint {US5}`() {
        assertThat(OpenObserveSite.US5.intakeEndpoint).isEqualTo("https://browser-intake-us5-openobserve.ai")
    }

    @Test
    fun `M return intake endpoint W intakeEndpoint {US1-FED}`() {
        assertThat(OpenObserveSite.US1_FED.intakeEndpoint).isEqualTo("https://browser-intake-openobserve-gov.com")
    }

    @Test
    fun `M return intake endpoint W intakeEndpoint {US2-FED}`() {
        assertThat(OpenObserveSite.US2_FED.intakeEndpoint).isEqualTo("https://browser-intake-us2-openobserve-gov.com")
    }

    @Test
    fun `M return intake endpoint W intakeEndpoint {EU1}`() {
        assertThat(OpenObserveSite.EU1.intakeEndpoint).isEqualTo("https://browser-intake-openobserve.eu")
    }

    @Test
    fun `M return intake endpoint W intakeEndpoint {AP1}`() {
        assertThat(OpenObserveSite.AP1.intakeEndpoint).isEqualTo("https://browser-intake-ap1-openobserve.ai")
    }

    @Test
    fun `M return intake endpoint W intakeEndpoint {AP2}`() {
        assertThat(OpenObserveSite.AP2.intakeEndpoint).isEqualTo("https://browser-intake-ap2-openobserve.ai")
    }

    @Test
    fun `M return intake endpoint W intakeEndpoint {STAGING}`() {
        assertThat(OpenObserveSite.STAGING.intakeEndpoint).isEqualTo("https://browser-intake-openobserve.com")
    }
}
