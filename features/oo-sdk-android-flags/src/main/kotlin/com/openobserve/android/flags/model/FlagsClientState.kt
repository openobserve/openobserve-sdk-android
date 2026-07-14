/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.flags.model

/**
 * Represents the current state of a [com.openobserve.android.flags.FlagsClient].
 */
sealed class FlagsClientState {
    /**
     * The client is not ready to evaluate flags.
     *
     * This state occurs before the first [setEvaluationContext()] call.
     * Maps to OpenFeature's [NOT_READY] state.
     */
    object NotReady : FlagsClientState()

    /**
     * The client has successfully loaded flags and they are available for evaluation.
     * This is the normal operational state.
     */
    object Ready : FlagsClientState()

    /**
     * The client is currently fetching new flags for a context change.
     * Cached flags may still be available for evaluation during this state.
     */
    object Reconciling : FlagsClientState()

    /**
     * The client is currently stale.
     * Cached flags may still be available for evaluation during this state.
     */
    object Stale : FlagsClientState()

    /**
     * An unrecoverable error has occurred.
     * The client cannot provide flag evaluations in this state.
     *
     * @param error The error that caused the transition to this state, or null if unknown.
     */
    data class Error(val error: Throwable? = null) : FlagsClientState()
}
