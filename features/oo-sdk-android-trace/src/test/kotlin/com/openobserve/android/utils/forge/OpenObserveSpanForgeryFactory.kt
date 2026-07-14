/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.utils.forge

import com.openobserve.android.trace.api.span.OpenObserveSpan
import com.openobserve.android.trace.api.span.OpenObserveSpanContext
import com.openobserve.android.trace.internal.OpenObserveTraceIdAdapter
import com.openobserve.trace.api.DDTraceId
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

internal class OpenObserveSpanForgeryFactory : ForgeryFactory<OpenObserveSpan> {

    override fun getForgery(forge: Forge): OpenObserveSpan {
        val rootSpan = forge.createSpan(context = forge.getForgery<OpenObserveSpanContext>())
        return forge.createSpan(context = forge.getForgery<OpenObserveSpanContext>(), localSpan = rootSpan)
    }

    private fun Forge.createSpan(context: OpenObserveSpanContext, localSpan: OpenObserveSpan? = null) = mock<OpenObserveSpan> {
        on { isRootSpan } doReturn aBool()
        on { isError } doReturn aBool()
        on { resourceName } doReturn aString()
        on { serviceName } doReturn aString()
        on { operationName } doReturn aString()
        on { traceId } doReturn OpenObserveTraceIdAdapter(getForgery<DDTraceId>())
        on { parentSpanId } doReturn aLong()
        on { samplingPriority } doReturn anInt()
        on { durationNano } doReturn aLong()
        on { startTimeNanos } doReturn aLong()
        on { localRootSpan } doReturn localSpan
        on { context() } doReturn context
    }
}
