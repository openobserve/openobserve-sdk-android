/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.utils.forge

import com.openobserve.android.core.configuration.Configuration
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class ConfigurationForgeryFactory :
    ForgeryFactory<Configuration> {
    override fun getForgery(forge: Forge): Configuration {
        return Configuration(
            coreConfig = forge.getForgery(),
            clientToken = forge.anHexadecimalString(),
            env = forge.aStringMatching("[a-zA-Z0-9_:./-]{0,195}[a-zA-Z0-9_./-]"),
            variant = forge.anElementFrom(forge.anAlphabeticalString(), ""),
            service = forge.aStringMatching("[a-z]+(\\.[a-z]+)+"),
            crashReportsEnabled = forge.aBool(),
            additionalConfig = forge.aMap { aString() to aString() },
            version = forge.aNullable { forge.anAlphaNumericalString() }
        )
    }
}
