/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sample.data.db.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
internal interface LogDao {

    @Query(value = "SELECT * FROM logs where ttl >= :minTtl")
    fun getAll(minTtl: Long): List<LogRoom>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(logs: List<LogRoom>)

    @Query("DELETE FROM logs where ttl < :minTtl")
    fun purge(minTtl: Long)
}
