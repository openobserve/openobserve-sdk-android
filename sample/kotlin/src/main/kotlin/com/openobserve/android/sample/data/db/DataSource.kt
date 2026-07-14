/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sample.data.db

import com.openobserve.android.sample.data.model.Log
import com.openobserve.android.sample.datalist.DataSourceType
import io.reactivex.rxjava3.core.SingleSource

internal interface DataSource {
    fun fetchLogs(): SingleSource<List<Log>>

    fun persistLogs(logs: List<Log>)

    val type: DataSourceType
}
