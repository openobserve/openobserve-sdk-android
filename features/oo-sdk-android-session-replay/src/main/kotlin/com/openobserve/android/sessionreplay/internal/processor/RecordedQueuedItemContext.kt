/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.internal.processor

import com.openobserve.android.sessionreplay.internal.utils.SessionReplayRumContext

internal data class RecordedQueuedItemContext(
    internal val timestamp: Long,
    internal val newRumContext: SessionReplayRumContext
)
