/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.api.feature

import androidx.annotation.AnyThread
import com.openobserve.android.api.context.OpenObserveContext
import com.openobserve.android.api.storage.EventBatchWriter
import com.openobserve.android.api.storage.datastore.DataStoreHandler
import com.openobserve.android.lint.InternalApi

/**
 * Represents a OpenObserve feature.
 */
interface FeatureScope {

    /**
     * Property to enable interaction with the data store.
     */
    val dataStore: DataStoreHandler

    /**
     * Utility to write an event, asynchronously.
     * @param withFeatureContexts Feature contexts ([OpenObserveContext.featuresContext] property) to include
     * in the [OpenObserveContext] provided. The value should be the feature names as declared by [Feature.name].
     * Default is empty, meaning that no feature contexts will be included.
     * @param callback an operation called with an up-to-date [OpenObserveContext]
     * and an [EventWriteScope]. Callback will be executed on a single context processing worker thread. Execution of
     * [EventWriteScope] will be done on a worker thread from I/O pool.
     * [OpenObserveContext] will have a state created at the moment this method is called.
     */
    @AnyThread
    fun withWriteContext(
        withFeatureContexts: Set<String> = emptySet(),
        callback: (ooContext: OpenObserveContext, write: EventWriteScope) -> Unit
    )

    /**
     * Utility to read current [OpenObserveContext], asynchronously.
     * @param withFeatureContexts Feature contexts ([OpenObserveContext.featuresContext] property) to include
     * in the [OpenObserveContext] provided. The value should be the feature names as declared by [Feature.name].
     * Default is empty, meaning that no feature contexts will be included.
     * @param callback an operation called with an up-to-date [OpenObserveContext].
     * [OpenObserveContext] will have a state created at the moment this method is called.
     */
    @AnyThread
    fun withContext(
        withFeatureContexts: Set<String> = emptySet(),
        callback: (ooContext: OpenObserveContext) -> Unit
    )

    // TODO RUM-9852 Implement better passthrough mechanism for the JVM crash scenario
    /**
     * Same as [withWriteContext] but will be executed in the blocking manner.
     *
     * @param withFeatureContexts Feature contexts ([OpenObserveContext.featuresContext] property) to include
     * in the [OpenObserveContext] provided. The value should be the feature names as declared by [Feature.name].
     * Default is empty, meaning that no feature contexts will be included.
     *
     * **NOTE**: This API is for the internal use only and is not guaranteed to be stable.
     */
    @AnyThread
    @InternalApi
    fun getWriteContextSync(withFeatureContexts: Set<String> = emptySet()): Pair<OpenObserveContext, EventWriteScope>?

    /**
     * Send event to a given feature. It will be sent in a synchronous way.
     *
     * @param event Event to send.
     */
    fun sendEvent(event: Any)

    /**
     * Returns the original feature.
     */
    fun <T : Feature> unwrap(): T
}

/**
 * Scope for the event write operation which is invoked on the worker thread from I/O pool, which is different
 * from the context processing worker thread used for [FeatureScope.withWriteContext] callback invocation.
 */
typealias EventWriteScope = ((EventBatchWriter) -> Unit) -> Unit
