/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.utils.forge

import com.openobserve.android.trace.api.span.OpenObserveSpanContext
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class OpenObserveSpanContextForgeryFactory : ForgeryFactory<OpenObserveSpanContext> {
    override fun getForgery(forge: Forge): OpenObserveSpanContext = mock {
        on { traceId } doReturn forge.getForgery()
        on { spanId } doReturn forge.aLong()
        on { samplingPriority } doReturn forge.anInt()
        on { tags } doReturn forge.aMap { aString() to aString() }
    }
}
