/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.forge

import com.openobserve.android.sessionreplay.internal.recorder.Node
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class NodeForgeryFactory : ForgeryFactory<Node> {

    override fun getForgery(forge: Forge): Node {
        return Node(
            wireframes = forge.aList { getForgery() },
            children = forge.aList {
                Node(
                    wireframes = forge.aList { getForgery() },
                    parents = aList { getForgery() }
                )
            },
            parents = forge.aList { getForgery() }
        )
    }
}
