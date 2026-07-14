/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sample.widget

import android.app.IntentService
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.openobserve.android.rum.GlobalRumMonitor
import com.openobserve.android.rum.RumActionType
import com.openobserve.android.sample.R
import com.openobserve.android.sample.SampleApplication
import okhttp3.Request
import timber.log.Timber
import java.io.IOException

/**
 * An [WidgetIntentService] to showcase tracking interactions with a home screen widget.
 */
class WidgetIntentService : IntentService("WidgetIntentService") {

    @Deprecated("Deprecated in Java")
    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            LOAD_RANDOM_RESOURCE_ACTION -> {
                val widgetName = intent.getStringExtra(WIDGET_NAME_ARG)
                val widgetId = intent.getIntExtra(WIDGET_ID_ARG, 0)
                val hasRumContext = widgetId != 0 && widgetName != null

                if (hasRumContext) {
                    GlobalRumMonitor.get()
                        .startView(widgetId, widgetName ?: "OpenObserveWidget")
                    val clickedTargetName = intent.getStringExtra(WIDGET_CLICKED_TARGET_NAME)
                    if (clickedTargetName != null) {
                        GlobalRumMonitor.get()
                            .addAction(RumActionType.CLICK, clickedTargetName)
                    }
                }

                performRequest()

                if (hasRumContext) {
                    GlobalRumMonitor.get().stopView(widgetId)
                }
            }
            else -> {
            }
        }
    }

    private fun performRequest() {
        updateUIStatus(applicationContext, true)
        val okHttpClient = SampleApplication.getOkHttpClient(applicationContext)
        val builder = Request.Builder()
            .get()
            .url("https://www.openobserve.ai/")

        val request = builder.build()
        try {
            val response = okHttpClient.newCall(request).execute()
            val body = response.body
            if (body != null) {
                val content: String = body.string()
                // Necessary to consume the response
                Timber.d("Response: $content")
            }
        } catch (e: IOException) {
            Timber.e("Error: ${e.message}")
        }
        updateUIStatus(applicationContext, false)
    }

    private fun updateUIStatus(context: Context, isLoading: Boolean) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        appWidgetManager.getAppWidgetIds(
            ComponentName(
                context,
                OpenObserveWidgetsProvider::class.java
            )
        ).forEach {
            val remoteViews = RemoteViews(
                context.packageName,
                R.layout.openobserve_widget
            )
            val visibility = if (isLoading) View.VISIBLE else View.GONE
            remoteViews.setTextViewText(R.id.status_field, context.getText(R.string.loading))
            remoteViews.setViewVisibility(R.id.status_field, visibility)
            appWidgetManager.updateAppWidget(it, remoteViews)
        }
    }

    companion object {
        internal const val LOAD_RANDOM_RESOURCE_ACTION = "load_random_resource"
        internal const val WIDGET_ID_ARG = "widget_id"
        internal const val WIDGET_NAME_ARG = "widget_name"
        internal const val WIDGET_CLICKED_TARGET_NAME = "widget_clicked_target_name"
    }
}
