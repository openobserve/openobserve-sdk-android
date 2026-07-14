/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sample.viewpager

import androidx.core.os.bundleOf

internal class FragmentA : PagerChildFragment() {

    companion object {
        fun newInstance(): FragmentA {
            return FragmentA().apply {
                arguments = bundleOf(
                    ARG_PAGE_NAME to "Fragment A",
                    ARG_WEB_VIEW_URL to "https://openobserve.dev/browser-sdk-test-playground/webview-support/",
                    "fragmentClassName" to FragmentA::class.java.simpleName
                )
            }
        }
    }
}
