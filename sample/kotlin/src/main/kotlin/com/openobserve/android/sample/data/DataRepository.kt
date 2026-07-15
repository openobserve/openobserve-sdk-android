/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.sample.data

import com.openobserve.android.sample.data.db.LocalDataSource
import com.openobserve.android.sample.data.model.Log
import com.openobserve.android.sample.data.remote.RemoteDataSource
import com.openobserve.android.sample.datalist.DataSourceType
import com.openobserve.android.trace.GlobalOpenObserveTracer
import com.openobserve.android.trace.api.scope.OpenObserveScope
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

internal class DataRepository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) {

    @Suppress("SimpleRedundantLet")
    fun getLogs(query: String): Flowable<List<Log>> {
        var spanScope: OpenObserveScope? = null
        return Single.concat(
            localDataSource.fetchLogs(),
            remoteDataSource.getLogs(query)
                .map { it.data }
                .doOnSuccess {
                    localDataSource.persistLogs(it)
                }
                .doOnSubscribe {
                    val tracer = GlobalOpenObserveTracer.get()
                    val span = tracer
                        .buildSpan("Fetch recent logs")
                        .start()
                    spanScope = tracer.activateSpan(span)
                }
                .doFinally {
                    GlobalOpenObserveTracer.get().activeSpan()?.let {
                        it.finish()
                    }
                    spanScope?.close()
                }
        )
            .subscribeOn(Schedulers.io())
    }

    fun getDataSource(): DataSourceType {
        return localDataSource.getType()
    }

    fun setDataSource(type: DataSourceType) {
        localDataSource.setType(type)
    }
}
