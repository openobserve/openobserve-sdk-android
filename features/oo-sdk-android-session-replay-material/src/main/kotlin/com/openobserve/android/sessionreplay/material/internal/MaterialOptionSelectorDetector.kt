/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.material.internal

import android.view.ViewGroup
import com.openobserve.android.sessionreplay.recorder.OptionSelectorDetector

internal class MaterialOptionSelectorDetector : OptionSelectorDetector {

    override fun isOptionSelector(view: ViewGroup): Boolean {
        return view.id == com.google.android.material.R.id.mtrl_picker_header
    }
}
