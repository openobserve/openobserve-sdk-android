/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sample.viewpager

import androidx.core.os.bundleOf

internal class FragmentB : PagerChildFragment() {

    companion object {
        fun newInstance(): FragmentB {
            return FragmentB().apply {
                arguments = bundleOf(
                    ARG_PAGE_NAME to "Fragment B",
                    ARG_WEB_VIEW_URL to "https://openobserve.dev/browser-sdk-test-playground/webview-support/",
                    "fragmentClassName" to FragmentB::class.java.simpleName
                )
            }
        }
    }
}
