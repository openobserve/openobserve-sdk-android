/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.utils.forge

import com.openobserve.android.core.configuration.UploadFrequency
import com.openobserve.android.core.internal.configuration.DataUploadConfiguration
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class DataUploadConfigurationForgeryFactory : ForgeryFactory<DataUploadConfiguration> {
    override fun getForgery(forge: Forge): DataUploadConfiguration {
        val frequency: UploadFrequency = forge.getForgery()
        // we limit the size to avoid OOM errors inside our tests
        return DataUploadConfiguration(frequency, forge.anInt(min = 1, max = 200))
    }
}
