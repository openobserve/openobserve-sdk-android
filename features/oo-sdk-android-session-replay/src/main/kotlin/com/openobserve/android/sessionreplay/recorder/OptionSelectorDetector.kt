/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.recorder

import android.view.ViewGroup

/**
 * Detects if a [ViewGroup] is a parent of selectable UI elements
 * (e.g. TextView, CheckBoxes, etc.).
 * This interface is meant for internal usage but feel free to provide an implementation
 * through the [com.openobserve.android.sessionreplay.ExtensionSupport] if you need it.
 */
interface OptionSelectorDetector {

    /**
     * Checks and returns true if this [ViewGroup] is considered as a container of selectable UI
     * elements, otherwise returns false.
     */
    fun isOptionSelector(view: ViewGroup): Boolean
}
