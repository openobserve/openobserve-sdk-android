/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.material.forge

import com.openobserve.tools.unit.forge.BaseConfigurator
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.jvm.useJvmFactories

internal class ForgeConfigurator : BaseConfigurator() {

    override fun configure(forge: Forge) {
        super.configure(forge)

        // Session Replay
        forge.addFactory(ShapeWireframeForgeryFactory())
        forge.addFactory(TextWireframeForgeryFactory())
        forge.addFactory(WireframeForgeryFactory())
        forge.addFactory(ShapeWireframeMutationForgeryFactory())
        forge.addFactory(WireframeClipForgeryFactory())
        forge.addFactory(ShapeStyleForgeryFactory())
        forge.addFactory(ShapeBorderForgeryFactory())
        forge.addFactory(GlobalBoundsForgeryFactory())
        forge.addFactory(SystemInformationForgeryFactory())
        forge.addFactory(MappingContextForgeryFactory())

        forge.useJvmFactories()
    }
}
