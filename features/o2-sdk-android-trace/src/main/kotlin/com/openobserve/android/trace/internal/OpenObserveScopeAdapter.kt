/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */
package com.openobserve.android.trace.internal

import com.openobserve.android.trace.api.scope.OpenObserveScope
import com.openobserve.trace.bootstrap.instrumentation.api.AgentScope

internal class OpenObserveScopeAdapter(
    internal val delegate: AgentScope
) : OpenObserveScope {
    override fun close() = delegate.close()
}
