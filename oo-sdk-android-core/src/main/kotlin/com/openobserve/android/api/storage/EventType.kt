/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.api.storage

/**
 * The type of event being sent to storage.
 */
enum class EventType {
    /** A generic customer event (e.g.: log, span, …). */
    DEFAULT,

    /** A customer event related to a crash. */
    CRASH,

    /** An internal telemetry event to monitor the SDK's behavior and performances. */
    TELEMETRY
}
