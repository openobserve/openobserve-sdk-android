/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.trace.internal.storage

import com.openobserve.android.api.context.OpenObserveContext

/**
 * A class which can transform an object of type [T] into a formatted String.
 */
internal interface ContextAwareSerializer<T : Any> {
    /**
     * Serializes the data into a String.
     * @return the String representing the data or null if any exception occurs
     */
    fun serialize(ooContext: OpenObserveContext, model: T): String?
}
