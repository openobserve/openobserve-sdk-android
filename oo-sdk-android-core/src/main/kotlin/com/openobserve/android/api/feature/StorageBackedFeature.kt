/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.api.feature

import com.openobserve.android.api.net.RequestFactory
import com.openobserve.android.api.storage.FeatureStorageConfiguration

/**
 * Interface to be implemented by the feature, which requires storage, to be
 * registered with [SdkCore].
 */
interface StorageBackedFeature : Feature {

    /**
     * Provides an instance of [RequestFactory] for the given feature. Will be
     * called before [Feature.onInitialize].
     */
    val requestFactory: RequestFactory

    /**
     * Provides storage configuration for the given feature. Will be
     * called before [Feature.onInitialize].
     */
    val storageConfiguration: FeatureStorageConfiguration
}
