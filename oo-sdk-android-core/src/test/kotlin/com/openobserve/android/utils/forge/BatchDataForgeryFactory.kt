/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.utils.forge

import com.openobserve.android.core.internal.persistence.BatchData
import com.openobserve.android.core.internal.persistence.BatchId
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class BatchDataForgeryFactory : ForgeryFactory<BatchData> {
    override fun getForgery(forge: Forge): BatchData {
        return BatchData(
            id = BatchId(id = forge.anAlphaNumericalString()),
            data = forge.aList { getForgery() },
            metadata = forge.aNullable {
                forge.aString(
                    forge.anInt(min = 0, max = DATA_SIZE_LIMIT)
                ).toByteArray()
            }
        )
    }
    companion object {
        const val DATA_SIZE_LIMIT = 32
    }
}
