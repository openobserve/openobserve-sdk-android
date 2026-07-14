/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sample.about

import android.content.Context
import android.os.AsyncTask
import androidx.lifecycle.ViewModel
import com.openobserve.android.rum.resource.getAssetAsRumResource
import com.openobserve.android.rum.resource.getRawResAsRumResource
import com.openobserve.android.sample.R
import com.openobserve.android.trace.withinSpan
import java.io.BufferedReader

@Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
internal class AboutViewModel : ViewModel() {

    private var asyncAboutTask: AsyncTask<Unit, Unit, String>? = null
    private var asyncLicenseTask: AsyncTask<Unit, Unit, String>? = null

    fun getAboutText(
        context: Context,
        onDone: (String) -> Unit = {}
    ) {
        asyncAboutTask = LoadResourceTask(context, R.raw.about, onDone)
        asyncAboutTask?.execute()
    }

    fun getLicenseText(
        context: Context,
        onDone: (String) -> Unit = {}
    ) {
        asyncLicenseTask = LoadAssetTask(context, "license.txt", onDone)
        asyncLicenseTask?.execute()
    }

    fun stopAsyncOperations() {
        asyncLicenseTask?.cancel(true)
        asyncAboutTask?.cancel(true)
        asyncLicenseTask = null
        asyncAboutTask = null
    }

    private class LoadResourceTask(
        val context: Context,
        val id: Int,
        val onDone: (String) -> Unit = {}
    ) : AsyncTask<Unit, Unit, String>() {

        override fun doInBackground(vararg params: Unit): String {
            return withinSpan("LoadResource") {
                val inputStream = context.getRawResAsRumResource(id)

                inputStream.bufferedReader().use(BufferedReader::readText)
            }
        }

        override fun onPostExecute(result: String) {
            if (!isCancelled) {
                onDone(result)
            }
        }
    }

    private class LoadAssetTask(
        val context: Context,
        val fileName: String,
        val onDone: (String) -> Unit = {}
    ) : AsyncTask<Unit, Unit, String>() {

        override fun doInBackground(vararg params: Unit): String {
            return withinSpan("LoadAsset") {
                val inputStream = context.getAssetAsRumResource(fileName)

                inputStream.bufferedReader().use(BufferedReader::readText)
            }
        }

        override fun onPostExecute(result: String) {
            if (!isCancelled) {
                onDone(result)
            }
        }
    }
}
