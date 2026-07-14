/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.internal.recorder.obfuscator

import com.openobserve.android.sessionreplay.recorder.mapper.TextViewMapper

internal class FixedLengthStringObfuscator : StringObfuscator {
    override fun obfuscate(stringValue: String): String {
        return TextViewMapper.FIXED_INPUT_MASK
    }
}
