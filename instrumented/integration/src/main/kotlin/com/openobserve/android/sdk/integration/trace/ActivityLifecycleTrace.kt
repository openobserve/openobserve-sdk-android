/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.android.sdk.integration.trace

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.openobserve.android.OpenObserve
import com.openobserve.android.api.context.OpenObserveContext
import com.openobserve.android.core.InternalSdkCore
import com.openobserve.android.log.Logs
import com.openobserve.android.sdk.integration.R
import com.openobserve.android.sdk.integration.RuntimeConfig
import com.openobserve.android.sdk.utils.getForgeSeed
import com.openobserve.android.sdk.utils.getTrackingConsent
import com.openobserve.android.trace.Trace
import com.openobserve.android.trace.api.span.OpenObserveSpan
import com.openobserve.android.trace.api.tracer.OpenObserveTracer
import fr.xgouchet.elmyr.Forge
import java.util.LinkedList
import java.util.Random

internal class ActivityLifecycleTrace : AppCompatActivity() {

    private val forge by lazy { Forge().apply { seed = intent.getForgeSeed() } }

    private lateinit var tracer: OpenObserveTracer
    private val sentSpans = LinkedList<OpenObserveSpan>()
    private val sentLogs = LinkedList<Pair<Int, String>>()
    private lateinit var activityStartSpan: OpenObserveSpan
    private lateinit var activityResumeSpan: OpenObserveSpan

    // region Activity

    @Suppress("CheckInternal")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val config = RuntimeConfig.configBuilder().build()
        val trackingConsent = intent.getTrackingConsent()

        OpenObserve.setVerbosity(Log.VERBOSE)
        val sdkCore = checkNotNull(
            OpenObserve.initialize(this, config, trackingConsent)
        )

        listOf(
            { Logs.enable(RuntimeConfig.logsConfigBuilder().build(), sdkCore) },
            { Trace.enable(RuntimeConfig.tracesConfigBuilder().build(), sdkCore) }
        )
            .shuffled(Random(intent.getForgeSeed()))
            .forEach { it() }

        tracer = RuntimeConfig.tracer(sdkCore)
        setContentView(R.layout.main_activity_layout)
    }

    override fun onStart() {
        super.onStart()
        activityStartSpan = buildSpan(forge.anAlphabeticalString())
    }

    override fun onResume() {
        super.onResume()
        activityResumeSpan = buildSpan(forge.anAlphabeticalString())
    }

    override fun onPause() {
        super.onPause()
        activityResumeSpan.finish()
    }

    override fun onStop() {
        super.onStop()
        activityStartSpan.finish()
    }

    // endregion

    // region Tests

    fun getSentSpans(): LinkedList<OpenObserveSpan> {
        return sentSpans
    }

    fun getSentLogs(): LinkedList<Pair<Int, String>> {
        return sentLogs
    }

    fun getOpenObserveContext(): OpenObserveContext? {
        return (OpenObserve.getInstance() as InternalSdkCore).getOpenObserveContext()
    }

    // endregion

    // region Internal

    private fun buildSpan(title: String): OpenObserveSpan {
        val span = tracer.buildSpan(title).start()
        checkNotNull(tracer.activateSpan(span)) { "Span activation failed" }
        val ddSpan = tracer.activeSpan() as OpenObserveSpan
        ddSpan.logMessage(title)
        sentLogs.add(Log.VERBOSE to title)
        sentSpans.add(ddSpan)
        return ddSpan
    }

    // endregion
}
