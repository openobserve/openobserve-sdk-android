/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.internal.time

/**
 * This object contains constant values for all the OpenObserve NTP Endpoint urls used in the SDK.
 */
internal enum class OpenObserveNtpEndpoint(val host: String) {

    /**
     * Endpoint for the Network Time Protocol time syncing.
     */
    NTP_0("0.datadog.pool.ntp.org"),

    /**
     * Endpoint for the Network Time Protocol time syncing.
     */
    NTP_1("1.datadog.pool.ntp.org"),

    /**
     * Endpoint for the Network Time Protocol time syncing.
     */
    NTP_2("2.datadog.pool.ntp.org"),

    /**
     * Endpoint for the Network Time Protocol time syncing.
     */
    NTP_3("3.datadog.pool.ntp.org")
}
