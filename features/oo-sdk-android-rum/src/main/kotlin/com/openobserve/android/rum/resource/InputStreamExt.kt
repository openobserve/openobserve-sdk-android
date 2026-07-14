/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.rum.resource

import com.openobserve.android.OpenObserve
import com.openobserve.android.api.SdkCore
import com.openobserve.android.rum.RumMonitor
import java.io.InputStream

/**
 * Allow the [RumMonitor] to track this [InputStream] as a RUM Resource.
 *
 * @param url the url to be associated with this resource
 * @param sdkCore the SDK instance to use. If not provided, default instance will be used.
 */
fun InputStream.asRumResource(url: String, sdkCore: SdkCore = OpenObserve.getInstance()): InputStream {
    return RumResourceInputStream(this, url, sdkCore)
}
