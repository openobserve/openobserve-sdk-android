/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.utils.forge

import com.openobserve.android.rum.internal.domain.scope.RumRawEvent
import com.openobserve.android.rum.model.ActionEvent
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory
import java.util.UUID

internal class ActionSentForgeryFactory : ForgeryFactory<RumRawEvent.ActionSent> {
    override fun getForgery(forge: Forge): RumRawEvent.ActionSent {
        return RumRawEvent.ActionSent(
            viewId = forge.getForgery<UUID>().toString(),
            frustrationCount = forge.anInt(min = 0),
            type = forge.aValueFrom(ActionEvent.ActionEventActionType::class.java),
            eventEndTimestampInNanos = forge.aPositiveLong()
        )
    }
}
