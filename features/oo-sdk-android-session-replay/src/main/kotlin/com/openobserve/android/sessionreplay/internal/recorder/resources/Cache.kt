/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.internal.recorder.resources

internal interface Cache<K : Any, V : Any> {

    fun put(value: V) {}
    fun put(key: K, value: V) {}
    fun get(key: K): V? = null
    fun size(): Int
    fun clear()

    companion object {
        internal const val DOES_NOT_IMPLEMENT_COMPONENTCALLBACKS =
            "Cache instance does not implement ComponentCallbacks2"
    }
}
