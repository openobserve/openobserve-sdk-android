/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.integration.tests

import com.openobserve.android.core.integration.tests.utils.MockWebServerWrapper
import org.junit.AfterClass
import org.junit.BeforeClass
import org.mockito.internal.matchers.Any
import java.util.concurrent.atomic.AtomicBoolean

abstract class MockServerTest : BaseTest() {

    fun getMockServerWrapper(): MockWebServerWrapper {
        return mockServerWrapper
    }

    companion object {
        private val mockServerStarted = AtomicBoolean(false)
        private val MOCK_SERVER_LOCK = Any()
        protected var mockServerWrapper: MockWebServerWrapper = MockWebServerWrapper()

        @BeforeClass
        @JvmStatic
        fun setupTestSuite() {
            if (mockServerStarted.compareAndSet(false, true)) {
                synchronized(MOCK_SERVER_LOCK) {
                    mockServerWrapper.start()
                }
            }
        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            if (mockServerStarted.compareAndSet(true, false)) {
                // we reinitialize the mock server as it cannot be reused
                synchronized(MOCK_SERVER_LOCK) {
                    mockServerWrapper.shutdown()
                    mockServerWrapper = MockWebServerWrapper()
                }
            }
        }

        fun cleanMockWebServer() {
            mockServerWrapper.clearData()
        }
    }
}
