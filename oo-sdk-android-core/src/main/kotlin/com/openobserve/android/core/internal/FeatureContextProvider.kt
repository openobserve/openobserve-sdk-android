/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.internal

internal fun interface FeatureContextProvider {
    /**
     * Returns **frozen** snapshot of the feature context which is not mutated over the time.
     */
    fun getFeatureContext(featureName: String): Map<String, Any?>
}
