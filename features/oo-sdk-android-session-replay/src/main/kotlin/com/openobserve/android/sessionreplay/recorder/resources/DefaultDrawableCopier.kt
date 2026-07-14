/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.recorder.resources

import android.content.res.Resources
import android.graphics.drawable.Drawable

/**
 * Default implementation of [DrawableCopier] interface, it copies the drawable from constant state.
 */
class DefaultDrawableCopier : DrawableCopier {
    override fun copy(originalDrawable: Drawable, resources: Resources): Drawable? {
        return originalDrawable.constantState?.newDrawable(resources)
    }
}
