/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.internal.recorder.mapper

import android.widget.CheckBox
import com.openobserve.android.api.InternalLogger
import com.openobserve.android.sessionreplay.recorder.mapper.TextViewMapper
import com.openobserve.android.sessionreplay.utils.ColorStringFormatter
import com.openobserve.android.sessionreplay.utils.DrawableToColorMapper
import com.openobserve.android.sessionreplay.utils.ViewBoundsResolver
import com.openobserve.android.sessionreplay.utils.ViewIdentifierResolver

internal open class CheckBoxMapper(
    textWireframeMapper: TextViewMapper<CheckBox>,
    viewIdentifierResolver: ViewIdentifierResolver,
    colorStringFormatter: ColorStringFormatter,
    viewBoundsResolver: ViewBoundsResolver,
    drawableToColorMapper: DrawableToColorMapper,
    internalLogger: InternalLogger
) : CheckableCompoundButtonMapper<CheckBox>(
    textWireframeMapper,
    viewIdentifierResolver,
    colorStringFormatter,
    viewBoundsResolver,
    drawableToColorMapper,
    internalLogger
)
