/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.metric.interactiontonextview

import com.openobserve.tools.annotation.NoOpImplementation

/**
 * Interface for identifying the last interaction in the previous view.
 */
@NoOpImplementation
interface LastInteractionIdentifier {
    /**
     * Validates the last interaction in the previous view.
     * @param context of the last interaction to validate
     */
    fun validate(context: PreviousViewLastInteractionContext): Boolean
}
