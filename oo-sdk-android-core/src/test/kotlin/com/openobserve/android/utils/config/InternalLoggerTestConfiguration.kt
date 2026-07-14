/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.utils.config

import com.openobserve.android.api.InternalLogger
import com.openobserve.android.core.internal.utils.unboundInternalLogger
import com.openobserve.tools.unit.extensions.config.TestConfiguration
import fr.xgouchet.elmyr.Forge
import org.mockito.kotlin.mock

internal class InternalLoggerTestConfiguration : TestConfiguration {

    lateinit var mockInternalLogger: InternalLogger

    private lateinit var originalInternalLogger: InternalLogger

    override fun setUp(forge: Forge) {
        mockInternalLogger = mock()

        originalInternalLogger = unboundInternalLogger

        unboundInternalLogger = mockInternalLogger
    }

    override fun tearDown(forge: Forge) {
        unboundInternalLogger = originalInternalLogger
    }
}
