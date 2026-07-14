/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.api.storage.datastore

import com.openobserve.android.core.persistence.datastore.DataStoreContent

/**
 * Callback for asynchronous read operations on the datastore.
 * @param T the datatype being retrieved.
 */
interface DataStoreReadCallback<T : Any> {

    /**
     * Triggered on successfully reading data from the datastore.
     *
     * @param dataStoreContent (nullable) contains the datastore content if there was data to fetch, else null.
     */
    fun onSuccess(dataStoreContent: DataStoreContent<T>?)

    /**
     * Triggered on failing to read data from the datastore.
     */
    fun onFailure()
}
