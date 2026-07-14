/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sample.data.db.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.openobserve.android.sample.data.db.OpenObserveDbContract

@Database(entities = [LogRoom::class], version = OpenObserveDbContract.DB_VERSION, exportSchema = false)
internal abstract class LogsDatabase : RoomDatabase() {

    abstract fun logDao(): LogDao

    companion object {

        @Volatile
        private var INSTANCE: LogsDatabase? = null

        fun getInstance(context: Context): LogsDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context): LogsDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                LogsDatabase::class.java,
                OpenObserveDbContract.DB_NAME
            ).build()
    }
}
