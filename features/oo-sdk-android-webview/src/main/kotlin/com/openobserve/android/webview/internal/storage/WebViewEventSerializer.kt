/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.webview.internal.storage

import com.openobserve.android.core.persistence.Serializer
import com.google.gson.JsonObject

internal class WebViewEventSerializer :
    Serializer<JsonObject> {

    override fun serialize(model: JsonObject): String {
        return model.toString()
    }
}
