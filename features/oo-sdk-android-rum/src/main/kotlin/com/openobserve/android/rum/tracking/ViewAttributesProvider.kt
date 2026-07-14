/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.tracking

import android.view.View
import com.openobserve.android.rum.RumAttributes

/**
 * Provides the extra attributes for the  as Map<String,Any?>.
 */
interface ViewAttributesProvider {

    /**
     * Add extra attributes to the default attributes Map.
     * @param view the [View].
     * @param attributes the default attributes Map. Usually this contains some default
     * attributes which are determined and added by the SDK. Please make sure you do not
     * override any of these reserved attributes.
     * @see [RumAttributes.TAG_TARGET_RESOURCE_ID]
     * @see [RumAttributes.TAG_TARGET_CLASS_NAME]
     */
    fun extractAttributes(view: View, attributes: MutableMap<String, Any?>)
}
