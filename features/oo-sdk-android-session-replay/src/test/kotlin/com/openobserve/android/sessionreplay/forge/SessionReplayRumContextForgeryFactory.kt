/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.forge

import com.openobserve.android.sessionreplay.internal.utils.SessionReplayRumContext
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory
import java.util.UUID

internal class SessionReplayRumContextForgeryFactory : ForgeryFactory<SessionReplayRumContext> {
    override fun getForgery(forge: Forge): SessionReplayRumContext {
        return SessionReplayRumContext(
            applicationId = forge.getForgery<UUID>().toString(),
            sessionId = forge.getForgery<UUID>().toString(),
            viewId = forge.getForgery<UUID>().toString(),
            viewTimeOffsetMs = forge.aLong(-10000, 10000)
        )
    }
}
