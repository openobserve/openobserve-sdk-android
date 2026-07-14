/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.recorder

import android.view.View
import androidx.annotation.UiThread
import com.openobserve.android.sessionreplay.model.MobileSegment

/**
 * Interface to define the callback for Jetpack Compose semantics tree to call
 * when there is an interop view to map.
 */
interface InteropViewCallback {

    /**
     * Called when an interop view needs to be mapped.
     */
    @UiThread
    fun map(view: View, mappingContext: MappingContext): List<MobileSegment.Wireframe>
}
