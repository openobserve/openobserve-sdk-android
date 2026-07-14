/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum

/**
 * Describes the type of a method associated with resource call.
 * @see [RumMonitor]
 */
enum class RumResourceMethod {
    /**
     * POST Method.
     */
    POST,

    /**
     * GET Method.
     */
    GET,

    /**
     * HEAD Method.
     */
    HEAD,

    /**
     * PUT Method.
     */
    PUT,

    /**
     * DELETE Method.
     */
    DELETE,

    /**
     * PATCH Method.
     */
    PATCH,

    /**
     * TRACE Method.
     */
    TRACE,

    /**
     * OPTIONS Method.
     */
    OPTIONS,

    /**
     * CONNECT Method.
     */
    CONNECT
}
