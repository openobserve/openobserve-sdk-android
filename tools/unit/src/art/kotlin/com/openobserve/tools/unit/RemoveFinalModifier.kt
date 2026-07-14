/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.tools.unit

import android.annotation.SuppressLint
import java.lang.reflect.Field
import java.lang.reflect.Modifier

internal object RemoveFinalModifier {
    @SuppressLint("DiscouragedPrivateApi")
    fun remove(field: Field) {
        Field::class.java.getDeclaredField("accessFlags")
            .apply {
                isAccessible = true
            }
            .set(field, field.modifiers and Modifier.FINAL.inv())
    }
}
