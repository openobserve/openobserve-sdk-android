/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.utils.forge

import com.openobserve.android.tests.elmyr.useCoreFactories
import com.openobserve.tools.unit.forge.BaseConfigurator
import fr.xgouchet.elmyr.Forge

internal class Configurator : BaseConfigurator() {

    override fun configure(forge: Forge) {
        super.configure(forge)

        // Core
        forge.useCoreFactories()

        // APM
        forge.addFactory(SpanEventForgeryFactory())
        forge.addFactory(TraceConfigurationForgeryFactory())
        forge.addFactory(CoreDDSpanForgeryFactory())
        forge.addFactory(AgentSpanLinkForgeryFactory())
        forge.addFactory(OpenObserveSpanLinkForgery())
        forge.addFactory(DDTraceIdForgeryFactory())
        forge.addFactory(OpenObserveSpanForgeryFactory())
        forge.addFactory(OpenObserveTraceIdForgeryFactory())
        forge.addFactory(OpenObserveSpanContextForgeryFactory())
    }
}
