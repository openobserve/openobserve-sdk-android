/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.utils.forge

import com.openobserve.android.rum.RumConfiguration
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory
import java.util.UUID

class RumConfigurationForgeryFactory : ForgeryFactory<RumConfiguration> {
    override fun getForgery(forge: Forge): RumConfiguration {
        return RumConfiguration(
            applicationId = forge.getForgery<UUID>().toString(),
            featureConfiguration = forge.getForgery()
        )
    }
}
