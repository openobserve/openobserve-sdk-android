/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.okhttp.internal.utils.forge

import com.openobserve.android.trace.internal.net.TraceContext
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class TraceContextFactory : ForgeryFactory<TraceContext> {

    override fun getForgery(forge: Forge): TraceContext {
        val fakeTraceId = forge.aStringMatching("[0-9a-f]{31}")
        val fakeSpanId = forge.aStringMatching("[0-9a-f]{16}")
        val fakeSamplingPriority = forge.anInt()
        return TraceContext(
            traceId = fakeTraceId,
            spanId = fakeSpanId,
            samplingPriority = fakeSamplingPriority
        )
    }
}
