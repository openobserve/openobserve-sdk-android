/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.internal.recorder.obfuscator

import android.os.Build
import androidx.annotation.RequiresApi
import com.openobserve.android.sessionreplay.internal.recorder.obfuscator.StringObfuscator.Companion.CHARACTER_MASK

/**
 * String obfuscator relying on Android N APIs to properly handle strings with emojis.
 */
@RequiresApi(Build.VERSION_CODES.N)
internal class AndroidNStringObfuscator : StringObfuscator {

    override fun obfuscate(stringValue: String): String {
        return buildString(stringValue.length) {
            stringValue.codePoints().forEach {
                if (Character.isWhitespace(it)) {
                    // I don't think we should log this case here. I could not even reproduce it
                    // in my tests. As long as there is a valid sdtring there there should not be
                    // any problem.
                    @Suppress("SwallowedException")
                    try {
                        append(Character.toChars(it))
                    } catch (e: IllegalArgumentException) {
                        append(CHARACTER_MASK)
                    }
                } else {
                    append(CHARACTER_MASK)
                }
            }
        }
    }
}
