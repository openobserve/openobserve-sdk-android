/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.utils.forge

import com.openobserve.android.core.internal.persistence.Batch
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class BatchForgeryFactory : ForgeryFactory<Batch> {
    override fun getForgery(forge: Forge): Batch {
        return Batch(
            forge.anHexadecimalString(),
            forge.anAlphabeticalString().toByteArray(Charsets.UTF_8)
        )
    }
}
