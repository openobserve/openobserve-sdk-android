/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.profiling.forge

import com.openobserve.android.internal.tests.elmyr.ProfilerStopEventTTIDFactory
import com.openobserve.android.internal.tests.elmyr.TTIDRumContextFactory
import com.openobserve.android.profiling.ExperimentalProfilingApi
import com.openobserve.android.tests.elmyr.useCoreFactories
import com.openobserve.tools.unit.forge.BaseConfigurator
import fr.xgouchet.elmyr.Forge

@OptIn(ExperimentalProfilingApi::class)
class Configurator : BaseConfigurator() {

    override fun configure(forge: Forge) {
        super.configure(forge)
        forge.useCoreFactories()
        forge.addFactory(ProfilingConfigurationForgeryFactory())
        forge.addFactory(PerfettoResultFactory())
        forge.addFactory(ProfilerStopEventTTIDFactory())
        forge.addFactory(TTIDRumContextFactory())
    }
}
