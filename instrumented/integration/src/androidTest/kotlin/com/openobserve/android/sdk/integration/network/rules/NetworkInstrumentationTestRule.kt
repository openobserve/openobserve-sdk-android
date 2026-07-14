/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sdk.integration.network.rules

import androidx.test.platform.app.InstrumentationRegistry
import com.openobserve.android.OpenObserve
import com.openobserve.android._InternalProxy
import com.openobserve.android.api.SdkCore
import com.openobserve.android.core.configuration.Configuration
import com.openobserve.android.privacy.TrackingConsent
import com.openobserve.android.sdk.integration.network.utils.TestEchoWebServer
import com.openobserve.android.trace.OpenObserveTracing
import com.openobserve.android.trace.GlobalOpenObserveTracer
import com.openobserve.android.trace.Trace
import com.openobserve.android.trace.TraceConfiguration
import fr.xgouchet.elmyr.Forge
import org.junit.rules.ExternalResource

/**
 * JUnit Rule for network instrumentation integration tests.
 *
 * Manages [com.openobserve.android.sdk.integration.network.utils.TestEchoWebServer] and OpenObserve SDK initialization.
 */
internal class NetworkInstrumentationTestRule : ExternalResource() {

    private var sdkCore: SdkCore? = null
    private val mockWebServer = TestEchoWebServer()

    val forge = Forge()

    /**
     * Base URL for HTTP requests to the mock server.
     */
    val baseUrl: String
        get() = mockWebServer.baseUrl

    override fun before() {
        mockWebServer.start()
        setupOpenObserveSdk()
    }

    override fun after() {
        GlobalOpenObserveTracer.clear()
        OpenObserve.stopInstance()
        mockWebServer.shutdown()

        InstrumentationRegistry
            .getInstrumentation()
            .targetContext
            .cacheDir
            .deleteRecursively()
    }

    private fun setupOpenObserveSdk() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val sdkConfig = Configuration.Builder(
            clientToken = FAKE_CLIENT_TOKEN,
            env = FAKE_ENV
        )
            .apply { _InternalProxy.allowClearTextHttp(this) }
            .build()

        sdkCore = checkNotNull(
            OpenObserve.initialize(context, sdkConfig, TrackingConsent.GRANTED)
        ) { "Failed to initialize OpenObserve SDK" }

        Trace.enable(
            TraceConfiguration.Builder()
                .build()
        )

        GlobalOpenObserveTracer.registerIfAbsent(
            OpenObserveTracing.newTracerBuilder()
                .withPartialFlushMinSpans(1)
                .build()
        )
    }

    companion object {
        private const val FAKE_CLIENT_TOKEN = "fake-token"
        private const val FAKE_ENV = "integration-test"
    }
}
