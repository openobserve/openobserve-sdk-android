/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.test.elmyr

import com.openobserve.android.core.persistence.PersistenceStrategy
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

class PersistenceStrategyBatchForgeryFactory : ForgeryFactory<PersistenceStrategy.Batch> {
    override fun getForgery(forge: Forge): PersistenceStrategy.Batch {
        return PersistenceStrategy.Batch(
            batchId = forge.anAlphabeticalString(),
            metadata = forge.aNullable { aString().toByteArray() },
            events = forge.aList { getForgery() }
        )
    }
}
