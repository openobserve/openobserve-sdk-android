/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.internal.utils

/**
 * Provides the [SessionReplayRumContext] into the Session Replay module.
 */
internal interface RumContextProvider {
    /**
     * Returns the current RUM context as [SessionReplayRumContext].
     */
    fun getRumContext(): SessionReplayRumContext
}
