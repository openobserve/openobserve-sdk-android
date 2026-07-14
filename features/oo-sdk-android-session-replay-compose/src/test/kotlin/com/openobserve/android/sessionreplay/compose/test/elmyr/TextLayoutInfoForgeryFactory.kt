/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.compose.test.elmyr

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import com.openobserve.android.sessionreplay.compose.internal.mappers.semantics.TextLayoutInfo
import com.openobserve.android.sessionreplay.model.MobileSegment
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class TextLayoutInfoForgeryFactory : ForgeryFactory<TextLayoutInfo> {
    override fun getForgery(forge: Forge): TextLayoutInfo {
        return TextLayoutInfo(
            text = forge.aString(),
            color = forge.aLong().toULong(),
            fontSize = forge.aSmallInt().toLong(),
            fontFamily = forge.anElementFrom(
                listOf(
                    FontFamily.Serif,
                    FontFamily.SansSerif,
                    FontFamily.Cursive,
                    FontFamily.Monospace,
                    FontFamily.Default
                )
            ),
            textAlign = forge.anElementFrom(TextAlign.values()),
            textOverflow = forge.aNullable { aValueFrom(MobileSegment.TruncationMode::class.java) }
        )
    }
}
