/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API

@Suppress("unused", "UndocumentedPublicClass") // used only by Lint tool
class OpenObserveIssueRegistry : IssueRegistry() {

    override val api = CURRENT_API

    // probably works with even lower APIs, but it needs to be checked
    override val minApi = ANDROID_STUDIO_7_3_API

    override val issues = listOf(
        InternalApiUsageDetector.ISSUE
    )

    override val vendor = Vendor(
        vendorName = "OpenObserve",
        feedbackUrl = "https://github.com/openobserve/openobserve-sdk-android/"
    )

    private companion object {
        const val ANDROID_STUDIO_7_3_API = 13
    }
}
