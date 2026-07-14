/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.trace.internal

import com.openobserve.android.trace.api.scope.OpenObserveScopeListener
import com.openobserve.trace.api.scopemanager.ScopeListener

internal class OpenObserveScopeListenerAdapter(
    internal val delegate: OpenObserveScopeListener
) : ScopeListener {
    override fun afterScopeClosed() = delegate.afterScopeClosed()
    override fun afterScopeActivated() = delegate.afterScopeActivated()
}
