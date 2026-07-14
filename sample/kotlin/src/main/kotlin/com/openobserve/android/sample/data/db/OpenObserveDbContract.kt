/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sample.data.db

internal object OpenObserveDbContract {

    const val DB_NAME = "OpenObserve.db"
    const val DB_VERSION = 1

    object Logs {
        const val TABLE_NAME = "logs"
        const val COLUMN_NAME_MESSAGE = "message"
        const val COLUMN_NAME_TIMESTAMP = "timestamp"
        const val COLUMN_NAME_TTL = "ttl"
    }
}
