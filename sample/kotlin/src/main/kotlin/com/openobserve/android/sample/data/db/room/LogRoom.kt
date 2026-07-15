/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.sample.data.db.room

import android.provider.BaseColumns
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.openobserve.android.sample.data.db.OpenObserveDbContract
import java.util.UUID

@Entity(tableName = OpenObserveDbContract.Logs.TABLE_NAME)
internal data class LogRoom(
    @PrimaryKey
    @ColumnInfo(name = BaseColumns._ID)
    val uid: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = OpenObserveDbContract.Logs.COLUMN_NAME_MESSAGE)
    val message: String,
    @ColumnInfo(name = OpenObserveDbContract.Logs.COLUMN_NAME_TIMESTAMP)
    val timestamp: String,
    @ColumnInfo(name = OpenObserveDbContract.Logs.COLUMN_NAME_TTL)
    val ttl: Long
)
