/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.tests.elmyr

import com.openobserve.android.api.storage.RawBatchEvent
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

class RawBatchEventForgeryFactory : ForgeryFactory<RawBatchEvent> {
    override fun getForgery(forge: Forge): RawBatchEvent {
        return RawBatchEvent(
            data = forge.aString(forge.anInt(1, EVENT_MAX_SIZE + 1)).toByteArray(),
            metadata = forge.aString(forge.anInt(0, METADATA_MAX_SIZE + 1)).toByteArray()
        )
    }

    companion object {
        const val EVENT_MAX_SIZE = 512
        const val METADATA_MAX_SIZE = 32
    }
}
