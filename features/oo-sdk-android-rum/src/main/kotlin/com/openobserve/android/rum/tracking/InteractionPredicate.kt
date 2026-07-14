/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.tracking

/**
 * Provides custom attributes for the events produced by the user action tracking strategy.
 */
interface InteractionPredicate {
    /**
     * Sets a custom name for the intercepted touch target.
     * @return the name to use for this touch target (if null or blank, the default will be used)
     */
    fun getTargetName(target: Any): String?
}
