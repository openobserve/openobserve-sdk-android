/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.api.instrumentation.network

/**
 * This interface indicates that the request supports non HTTP-specified methods.
 */
interface ExtendedRequestInfo {

    /**
     * Returns the tag attached with type as a key, or null if no tag is attached with that key.
     */
    fun <T> tag(type: Class<out T>): T?
}

/**
 * Returns the tag attached with type as a key, or null if no tag is attached with that key.
 */
fun <T> HttpRequestInfo.tag(type: Class<out T>): T? = if (this is ExtendedRequestInfo) this.tag(type) else null
