/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.internal.persistence

import com.openobserve.android.lint.InternalApi

/**
 * The Deserializer<P, R> generic interface. Should be implemented by any custom deserializer.
 *
 * FOR INTERNAL USAGE ONLY. THIS INTERFACE CONTENT MAY CHANGE WITHOUT NOTICE.
 */
@InternalApi
interface Deserializer<P : Any, R : Any> {

    /**
     * Deserializes the data from the given payload type into given output type.
     * @return the model represented by the given payload, or null when deserialization
     * is impossible.
     */
    @InternalApi
    fun deserialize(model: P): R?
}
