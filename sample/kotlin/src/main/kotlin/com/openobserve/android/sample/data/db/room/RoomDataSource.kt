/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sample.data.db.room

import android.content.Context
import com.openobserve.android.sample.data.db.DataSource
import com.openobserve.android.sample.data.model.Log
import com.openobserve.android.sample.data.model.LogAttributes
import com.openobserve.android.sample.datalist.DataSourceType
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

internal class RoomDataSource(val context: Context) : DataSource {

    private val logDao = LogsDatabase.getInstance(context).logDao()

    // region LocalDataSource

    override val type: DataSourceType = DataSourceType.ROOM

    override fun persistLogs(logs: List<Log>) {
        insertLogs(logs)
    }

    override fun fetchLogs(): Single<List<Log>> {
        return Single.fromCallable(fetchLogsCallable).subscribeOn(Schedulers.io())
    }

    // endregion

    // region Internal

    private fun insertLogs(logs: List<Log>) {
        val currentTimeInMillis = System.currentTimeMillis()
        val minTtlRequired = currentTimeInMillis - LOGS_EXPIRING_TTL_IN_MS
        // purge data first
        logDao.purge(minTtlRequired)
        // add new data
        logDao.insertAll(
            logs.map {
                LogRoom(
                    uid = it.id,
                    message = it.attributes.message,
                    timestamp = it.attributes.timestamp,
                    ttl = currentTimeInMillis
                )
            }
        )
    }

    private val fetchLogsCallable = Callable<List<Log>> {
        val minTtlRequired =
            System.currentTimeMillis() - LOGS_EXPIRING_TTL_IN_MS
        logDao.getAll(minTtlRequired).map {
            Log(
                id = it.uid,
                attributes = LogAttributes(message = it.message, timestamp = it.timestamp)
            )
        }
    }

    // endregion

    companion object {
        val LOGS_EXPIRING_TTL_IN_MS = TimeUnit.HOURS.toMillis(2)
    }
}
