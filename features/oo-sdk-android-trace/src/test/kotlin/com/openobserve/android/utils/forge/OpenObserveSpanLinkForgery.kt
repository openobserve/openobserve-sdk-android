/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.utils.forge

import com.openobserve.android.trace.api.span.OpenObserveSpanLink
import com.openobserve.android.trace.api.trace.OpenObserveTraceId
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class OpenObserveSpanLinkForgery : ForgeryFactory<OpenObserveSpanLink> {
    override fun getForgery(forge: Forge): OpenObserveSpanLink = object : OpenObserveSpanLink {
        override val spanId: Long = forge.aLong()
        override val sampled: Boolean = forge.aBool()
        override val traceStrace: String = forge.aString()
        override val attributes: Map<String, String> = forge.aMap { aString() to aString() }
        override val traceId: OpenObserveTraceId = forge.getForgery()
    }
}
