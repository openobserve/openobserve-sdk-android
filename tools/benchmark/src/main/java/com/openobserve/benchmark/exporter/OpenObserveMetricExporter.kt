/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.benchmark.exporter

import android.os.Build
import com.openobserve.android.BuildConfig
import com.openobserve.benchmark.OpenObserveExporterConfiguration
import com.openobserve.benchmark.internal.OpenObserveHttpClient
import com.openobserve.benchmark.internal.model.BenchmarkContext
import io.opentelemetry.sdk.common.CompletableResultCode
import io.opentelemetry.sdk.metrics.InstrumentType
import io.opentelemetry.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.sdk.metrics.data.MetricData
import io.opentelemetry.sdk.metrics.export.MetricExporter

internal class OpenObserveMetricExporter(ooExporterConfiguration: OpenObserveExporterConfiguration) : MetricExporter {

    private val benchmarkContext: BenchmarkContext = BenchmarkContext(
        deviceModel = Build.MODEL ?: UNKNOWN_TAG_VALUE,
        osVersion = Build.VERSION.RELEASE ?: UNKNOWN_TAG_VALUE,
        run = ooExporterConfiguration.run ?: UNKNOWN_TAG_VALUE,
        scenario = ooExporterConfiguration.scenario,
        applicationId = ooExporterConfiguration.applicationId ?: UNKNOWN_TAG_VALUE,
        intervalInSeconds = ooExporterConfiguration.intervalInSeconds,
        env = BuildConfig.BUILD_TYPE
    )
    private val metricHttpClient: OpenObserveHttpClient = OpenObserveHttpClient(
        benchmarkContext,
        ooExporterConfiguration
    )

    override fun getAggregationTemporality(instrumentType: InstrumentType): AggregationTemporality {
        return AggregationTemporality.DELTA
    }

    override fun export(metrics: MutableCollection<MetricData>): CompletableResultCode {
        // currently no aggregation is required
        metricHttpClient.uploadMetric(metrics.toList())
        return CompletableResultCode.ofSuccess()
    }

    override fun flush(): CompletableResultCode {
        // currently do nothing
        return CompletableResultCode.ofSuccess()
    }

    override fun shutdown(): CompletableResultCode {
        // currently do nothing
        return CompletableResultCode.ofSuccess()
    }

    companion object {
        private const val UNKNOWN_TAG_VALUE = "unknown"
    }
}
