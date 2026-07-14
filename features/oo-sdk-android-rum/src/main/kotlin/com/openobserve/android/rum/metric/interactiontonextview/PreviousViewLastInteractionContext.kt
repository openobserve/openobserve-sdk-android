/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.metric.interactiontonextview

import com.openobserve.android.rum.model.ActionEvent

/**
 * Represents the context of the last interaction in the previous view.
 * @property actionType The type of the last interaction.
 * @property eventCreatedAtNanos The timestamp (in nanoseconds) when the event was created in the previous view.
 * @property currentViewCreationTimestamp The timestamp (in nanoseconds) when the current view was created,
 * or null if not applicable.
 */
data class PreviousViewLastInteractionContext(
    val actionType: ActionEvent.ActionEventActionType,
    val eventCreatedAtNanos: Long,
    val currentViewCreationTimestamp: Long?
)
