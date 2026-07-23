/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.core.internal.thread

import com.openobserve.android.core.configuration.BackPressureStrategy
import com.openobserve.android.internal.time.TimeProvider
import fr.xgouchet.elmyr.Forge
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class BackPressureExecutorServiceTest :
    AbstractExecutorServiceTest<BackPressureExecutorService>() {

    override fun createTestedExecutorService(
        forge: Forge,
        backPressureStrategy: BackPressureStrategy,
        timeProvider: TimeProvider
    ): BackPressureExecutorService {
        return BackPressureExecutorService(
            mockInternalLogger,
            forge.anAlphabeticalString(),
            backPressureStrategy,
            timeProvider
        )
    }

    @Test
    fun `M use OpenObserveThreadFactory W constructor()`() {
        // Then
        assertThat(testedExecutor.threadFactory).isInstanceOf(OpenObserveThreadFactory::class.java)
    }
}
