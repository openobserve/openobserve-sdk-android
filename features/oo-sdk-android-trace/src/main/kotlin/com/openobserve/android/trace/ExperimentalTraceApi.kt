/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.trace

/**
 * Marks declarations that are part of the experimental Tracing API.
 * The API surface may change in future releases without prior notice.
 */
@RequiresOptIn(
    message = "This is an experimental Tracing API." +
        " It may change in the future.",
    level = RequiresOptIn.Level.WARNING
)
@Retention(AnnotationRetention.BINARY)
annotation class ExperimentalTraceApi
