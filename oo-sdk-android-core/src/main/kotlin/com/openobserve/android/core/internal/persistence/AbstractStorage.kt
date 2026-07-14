/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.core.internal.persistence

import androidx.annotation.AnyThread
import androidx.annotation.WorkerThread
import com.openobserve.android.api.InternalLogger
import com.openobserve.android.api.context.OpenObserveContext
import com.openobserve.android.api.feature.EventWriteScope
import com.openobserve.android.api.storage.EventBatchWriter
import com.openobserve.android.api.storage.EventType
import com.openobserve.android.api.storage.FeatureStorageConfiguration
import com.openobserve.android.api.storage.RawBatchEvent
import com.openobserve.android.core.internal.metrics.RemovalReason
import com.openobserve.android.core.internal.privacy.ConsentProvider
import com.openobserve.android.core.internal.utils.executeSafe
import com.openobserve.android.core.persistence.NoOpPersistenceStrategy
import com.openobserve.android.core.persistence.PersistenceStrategy
import com.openobserve.android.privacy.TrackingConsent
import com.openobserve.android.privacy.TrackingConsentProviderCallback
import java.util.concurrent.ExecutorService

internal class AbstractStorage(
    internal val sdkCoreId: String?,
    private val featureName: String,
    internal val persistenceStrategyFactory: PersistenceStrategy.Factory,
    private val executorService: ExecutorService,
    private val internalLogger: InternalLogger,
    internal val storageConfiguration: FeatureStorageConfiguration,
    consentProvider: ConsentProvider
) : Storage, TrackingConsentProviderCallback {

    private val grantedPersistenceStrategy: PersistenceStrategy by lazy {
        persistenceStrategyFactory.create(
            "$sdkCoreId/$featureName/${TrackingConsent.GRANTED}",
            storageConfiguration.maxItemsPerBatch,
            storageConfiguration.maxBatchSize
        )
    }

    private val pendingPersistenceStrategy: PersistenceStrategy by lazy {
        persistenceStrategyFactory.create(
            "$sdkCoreId/$featureName/${TrackingConsent.PENDING}",
            storageConfiguration.maxItemsPerBatch,
            storageConfiguration.maxBatchSize
        )
    }

    private val writeLock = Any()

    private val notGrantedPersistenceStrategy: PersistenceStrategy = NoOpPersistenceStrategy()

    init {
        @Suppress("LeakingThis")
        consentProvider.registerCallback(this)
    }

    // region Storage

    @AnyThread
    override fun getEventWriteScope(
        ooContext: OpenObserveContext
    ): EventWriteScope {
        val strategy = resolvePersistenceStrategy(ooContext)
        val writer = object : EventBatchWriter {
            @WorkerThread
            override fun currentMetadata(): ByteArray? {
                return strategy.currentMetadata()
            }

            @WorkerThread
            override fun write(event: RawBatchEvent, batchMetadata: ByteArray?, eventType: EventType): Boolean {
                return strategy.write(event, batchMetadata, eventType)
            }
        }
        // although we don't know what storage is backed by the persistence strategy, so maybe writing in a concurrent
        // way is fine there and lock is not needed, but taking precautions
        return AsyncEventWriteScope(executorService, writer, writeLock, featureName, internalLogger)
    }

    private fun resolvePersistenceStrategy(ooContext: OpenObserveContext) =
        when (ooContext.trackingConsent) {
            TrackingConsent.GRANTED -> grantedPersistenceStrategy
            TrackingConsent.PENDING -> pendingPersistenceStrategy
            TrackingConsent.NOT_GRANTED -> notGrantedPersistenceStrategy
        }

    @WorkerThread
    override fun readNextBatch(): BatchData? {
        return grantedPersistenceStrategy.lockAndReadNext()?.let {
            BatchData(
                id = BatchId(it.batchId),
                data = it.events,
                metadata = it.metadata
            )
        }
    }

    @WorkerThread
    override fun confirmBatchRead(
        batchId: BatchId,
        removalReason: RemovalReason,
        deleteBatch: Boolean
    ) {
        if (deleteBatch) {
            grantedPersistenceStrategy.unlockAndDelete(batchId.id)
        } else {
            grantedPersistenceStrategy.unlockAndKeep(batchId.id)
        }
    }

    @AnyThread
    override fun dropAll() {
        executorService.executeSafe("Data drop", internalLogger) {
            grantedPersistenceStrategy.dropAll()
            pendingPersistenceStrategy.dropAll()
        }
    }

    // endregion

    // region TrackingConsentProviderCallback

    override fun onConsentUpdated(
        previousConsent: TrackingConsent,
        newConsent: TrackingConsent
    ) {
        executorService.executeSafe("Data migration", internalLogger) {
            if (previousConsent == TrackingConsent.PENDING) {
                when (newConsent) {
                    TrackingConsent.GRANTED -> pendingPersistenceStrategy.migrateData(grantedPersistenceStrategy)
                    TrackingConsent.NOT_GRANTED -> pendingPersistenceStrategy.dropAll()
                    TrackingConsent.PENDING -> {
                        // Nothing to do
                    }
                }
            }
        }
    }

    // endregion
}
