/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.recorder.resources

import android.content.res.Resources
import android.graphics.drawable.Drawable

/**
 * Interface of copying drawable to a new one.
 */
fun interface DrawableCopier {

    /**
     * Called to copy the drawable.
     * @param originalDrawable the original drawable to copy
     * @param resources resources of the view.
     *
     * @return New copied drawable.
     */
    fun copy(originalDrawable: Drawable, resources: Resources): Drawable?
}
