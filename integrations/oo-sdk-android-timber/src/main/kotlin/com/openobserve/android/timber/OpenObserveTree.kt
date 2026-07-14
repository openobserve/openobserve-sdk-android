/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.timber

import com.openobserve.android.OpenObserve
import com.openobserve.android.api.SdkCore
import com.openobserve.android.log.Logger
import timber.log.Timber

/**
 * An implementation of a [Timber.Tree], forwarding all logs to the provided [Logger].
 *
 * @param logger the logger to use with Timber.
 */
class OpenObserveTree(
    private val logger: Logger
) : Timber.Tree() {

    /**
     * Creates a [Timber.Tree] with a default [Logger] having a minimum log priority
     * for OpenObserve logs set to specified value.
     *
     * See [Logger.Builder.setRemoteLogThreshold] for details.
     *
     * @param minLogPriority Minimum log threshold (priority) to be sent to the OpenObserve servers.
     * @param sdkCore SDK instance to bind to. If not provided, default instance will be used.
     */
    @Suppress("unused")
    @JvmOverloads
    constructor(minLogPriority: Int, sdkCore: SdkCore = OpenObserve.getInstance()) :
        this(
            Logger.Builder(sdkCore)
                .setRemoteLogThreshold(minLogPriority)
                .build()
        )

    init {
        logger.addTag("android:timber")
    }

    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?
    ) {
        val attributes = if (tag != null) {
            mapOf(TIMBER_TAG_ATTRIBUTE to tag)
        } else {
            emptyMap()
        }
        logger.log(priority, message, t, attributes)
    }

    private companion object {
        const val TIMBER_TAG_ATTRIBUTE = "timber.tag"
    }
}
