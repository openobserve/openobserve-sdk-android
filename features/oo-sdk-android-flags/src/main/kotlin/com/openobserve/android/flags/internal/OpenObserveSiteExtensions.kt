/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.flags.internal

import com.openobserve.android.OpenObserveSite

/**
 * Gets the complete flags endpoint URL.
 * @param customerDomain The customer-specific subdomain prefix for the flags CDN.
 * This is used to construct the full host in the format: `<customerDomain>.ff-cdn.<site>.<tld>`
 * @return Complete flags endpoint URL or null if site not supported
 */
internal fun OpenObserveSite.getFlagsEndpoint(customerDomain: String): String? {
    val host = flagsHost(customerDomain) ?: return null
    return "https://$host$FLAGS_PATH"
}

/**
 * Extension function that returns the flags CDN host for this OpenObserve site.
 * @param customerDomain The customer-specific subdomain prefix for the flags CDN
 * @return Flags host string in format `<customerDomain>.ff-cdn.<site>.<tld>`, or null if site not supported
 */
private fun OpenObserveSite.flagsHost(customerDomain: String): String? = when (this) {
    OpenObserveSite.US1_FED,
    OpenObserveSite.US2_FED -> null

    OpenObserveSite.STAGING -> "$customerDomain.ff-cdn.datad0g.com"
    OpenObserveSite.EU1 -> buildFlagsHostString(customerDomain, tld = "eu") // No site in the host, .eu TLD
    OpenObserveSite.US1 -> buildFlagsHostString(customerDomain) // No site in the host, default .com TLD

    OpenObserveSite.US3 -> buildFlagsHostString(customerDomain, dc = "us3", tld = "com")
    OpenObserveSite.US5 -> buildFlagsHostString(customerDomain, dc = "us5", tld = "com")
    OpenObserveSite.AP1 -> buildFlagsHostString(customerDomain, dc = "ap1", tld = "com")
    OpenObserveSite.AP2 -> buildFlagsHostString(customerDomain, dc = "ap2", tld = "com")
}

private fun buildFlagsHostString(customerDomain: String, dc: String? = null, tld: String = "com"): String {
    val parts = listOfNotNull(
        customerDomain,
        "ff-cdn",
        dc,
        "openobserve",
        tld
    )
    return parts.joinToString(".")
}

private const val FLAGS_PATH = "/precompute-assignments"
