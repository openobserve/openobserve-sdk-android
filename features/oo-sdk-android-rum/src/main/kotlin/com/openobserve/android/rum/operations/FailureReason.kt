/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.rum.operations

/**
 * Represents the possible reasons for a failed operation.
 */
enum class FailureReason {
    /**
     * Represents a failure caused by an error during execution.
     */
    ERROR,

    /**
     * Represents a failure caused by user or process abandonment.
     */
    ABANDONED,

    /**
     * Represents a failure due to other unspecified reasons.
     */
    OTHER
}
