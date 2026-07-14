/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.internal.persistence.datastore

import com.openobserve.android.api.storage.datastore.DataStoreHandler
import com.openobserve.android.api.storage.datastore.DataStoreReadCallback
import com.openobserve.android.api.storage.datastore.DataStoreWriteCallback
import com.openobserve.android.core.internal.persistence.Deserializer
import com.openobserve.android.core.persistence.Serializer

internal class NoOpDataStoreHandler : DataStoreHandler {
    override fun <T : Any> setValue(
        key: String,
        data: T,
        version: Int,
        callback: DataStoreWriteCallback?,
        serializer: Serializer<T>
    ) {
        // NoOp Implementation
    }

    override fun <T : Any> value(
        key: String,
        version: Int?,
        callback: DataStoreReadCallback<T>,
        deserializer: Deserializer<String, T>
    ) {
        // NoOp Implementation
    }

    override fun removeValue(
        key: String,
        callback: DataStoreWriteCallback?
    ) {
        // NoOp Implementation
    }

    override fun clearAllData() {
        // NoOp Implementation
    }
}
