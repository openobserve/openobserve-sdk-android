/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.utils.forge

import com.openobserve.android.rum.utils.forge.useCommonRumFactories
import com.openobserve.android.tests.elmyr.useCoreFactories
import com.openobserve.tools.unit.forge.BaseConfigurator
import fr.xgouchet.elmyr.Forge

internal class Configurator : BaseConfigurator() {
    override fun configure(forge: Forge) {
        super.configure(forge)

        // Core
        forge.useCoreFactories()

        // RUM
        forge.useCommonRumFactories()
        forge.addFactory(RumContextForgeryFactory())
    }
}
