/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark.exporter

import android.os.Build
import com.openobserve.android.BuildConfig
import com.openobserve.benchmark.OpenObserveExporterConfiguration
import com.openobserve.benchmark.internal.BenchmarkSpanToSpanEventMapper
import com.openobserve.benchmark.internal.OpenObserveHttpClient
import com.openobserve.benchmark.internal.model.BenchmarkContext
import io.opentelemetry.sdk.common.CompletableResultCode
import io.opentelemetry.sdk.trace.data.SpanData
import io.opentelemetry.sdk.trace.export.SpanExporter

internal class OpenObserveSpanExporter(ooExporterConfiguration: OpenObserveExporterConfiguration) : SpanExporter {

    private val benchmarkContext: BenchmarkContext = BenchmarkContext(
        deviceModel = Build.MODEL ?: UNKNOWN_TAG_VALUE,
        osVersion = Build.VERSION.RELEASE ?: UNKNOWN_TAG_VALUE,
        run = ooExporterConfiguration.run ?: UNKNOWN_TAG_VALUE,
        scenario = ooExporterConfiguration.scenario,
        applicationId = ooExporterConfiguration.applicationId ?: UNKNOWN_TAG_VALUE,
        intervalInSeconds = ooExporterConfiguration.intervalInSeconds,
        env = BuildConfig.BUILD_TYPE
    )

    private val httpClient: OpenObserveHttpClient = OpenObserveHttpClient(
        benchmarkContext,
        ooExporterConfiguration
    )

    private val benchmarkSpanToSpanEventMapper = BenchmarkSpanToSpanEventMapper()

    override fun export(spans: MutableCollection<SpanData>): CompletableResultCode {
        spans.map {
            benchmarkSpanToSpanEventMapper.map(benchmarkContext, it)
        }.apply {
            httpClient.uploadSpanEvent(this)
        }
        return CompletableResultCode.ofSuccess()
    }

    override fun flush(): CompletableResultCode {
        // do nothing
        return CompletableResultCode.ofSuccess()
    }

    override fun shutdown(): CompletableResultCode {
        // do nothing
        return CompletableResultCode.ofSuccess()
    }

    companion object {
        private const val UNKNOWN_TAG_VALUE = "unknown"
    }
}
