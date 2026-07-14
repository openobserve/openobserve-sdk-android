/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.utils.forge

import com.openobserve.android.core.internal.persistence.file.FilePersistenceConfig
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class FilePersistenceConfigForgeryFactory : ForgeryFactory<FilePersistenceConfig> {
    override fun getForgery(forge: Forge): FilePersistenceConfig {
        return FilePersistenceConfig(
            recentDelayMs = forge.aPositiveLong(),
            maxBatchSize = forge.aPositiveLong(),
            maxItemsPerBatch = forge.aBigInt(),
            oldFileThreshold = forge.aPositiveLong(),
            maxDiskSpace = forge.aPositiveLong()
        )
    }
}
