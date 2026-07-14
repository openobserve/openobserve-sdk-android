/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.internal.metric.interactiontonextview

import com.openobserve.android.rum.model.ActionEvent

internal data class InternalInteractionContext(
    internal val viewId: String,
    internal val actionType: ActionEvent.ActionEventActionType,
    internal val eventCreatedAtNanos: Long
)
