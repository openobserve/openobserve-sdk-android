/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.sample.datalist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.openobserve.android.OpenObserve
import com.openobserve.android.rx.sendErrorToOpenObserve
import com.openobserve.android.sample.data.DataRepository
import com.openobserve.android.sample.data.model.Log
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject

internal class DataListViewModel(val repository: DataRepository) : ViewModel() {

    private val uiRequestSubject: PublishSubject<UIRequest> = PublishSubject.create()
    private var disposable: Disposable? = null
    private val performRequestObservable: Observable<UIResponse> =
        uiRequestSubject
            .switchMap { request ->
                when (request) {
                    is UIRequest.FetchData -> {
                        val flowable = repository.getLogs("source:android")
                        flowable.sendErrorToOpenObserve(OpenObserve.getInstance())
                        flowable.toObservable()
                            .map<UIResponse> {
                                UIResponse.Success(it)
                            }
                            .onErrorReturn {
                                UIResponse.Error(it.message ?: "Unknown Error")
                            }
                    }
                }
            }
    private val liveData = MutableLiveData<UIResponse>()

    fun performRequest(request: UIRequest) {
        if (disposable == null) {
            disposable = performRequestObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    liveData.value = it
                }
        }
        uiRequestSubject.onNext(request)
    }

    fun observeLiveData(): LiveData<UIResponse> {
        return liveData
    }

    fun getDataSource(): DataSourceType {
        return repository.getDataSource()
    }

    fun selectDataSource(type: DataSourceType) {
        repository.setDataSource(type)
    }

    sealed class UIResponse {
        class Success(val data: List<Log>) : UIResponse()
        class Error(val message: String) : UIResponse()
    }

    sealed class UIRequest {
        object FetchData : UIRequest()
    }
}
