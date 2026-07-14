/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.utils.forge

import com.openobserve.android.log.LogsConfiguration
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory
import org.mockito.kotlin.mock

class LogsConfigurationForgeryFactory : ForgeryFactory<LogsConfiguration> {
    override fun getForgery(forge: Forge): LogsConfiguration {
        return LogsConfiguration(
            customEndpointUrl = forge.aNullable {
                aStringMatching("https://[a-z]+\\.com")
            },
            eventMapper = mock()
        )
    }
}
