/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.utils.forge

import com.openobserve.android.api.storage.FeatureStorageConfiguration
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class FeatureStorageConfigurationForgeryFactory :
    ForgeryFactory<FeatureStorageConfiguration> {
    override fun getForgery(forge: Forge): FeatureStorageConfiguration {
        return FeatureStorageConfiguration(
            maxBatchSize = forge.aPositiveLong(),
            maxItemsPerBatch = forge.aBigInt(),
            maxItemSize = forge.aPositiveLong(),
            oldBatchThreshold = forge.aPositiveLong()
        )
    }
}
