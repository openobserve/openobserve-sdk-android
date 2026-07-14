/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark.exporter

import android.os.Build
import android.util.Log
import com.openobserve.android.BuildConfig
import com.openobserve.benchmark.OpenObserveExporterConfiguration
import com.openobserve.benchmark.internal.MetricRequestBodyBuilder
import com.openobserve.benchmark.internal.model.BenchmarkContext
import io.opentelemetry.sdk.common.CompletableResultCode
import io.opentelemetry.sdk.metrics.InstrumentType
import io.opentelemetry.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.sdk.metrics.data.MetricData
import io.opentelemetry.sdk.metrics.export.MetricExporter

internal class LogcatMetricExporter(ooExporterConfiguration: OpenObserveExporterConfiguration) : MetricExporter {

    private val benchmarkContext: BenchmarkContext = BenchmarkContext(
        deviceModel = Build.MODEL,
        osVersion = Build.VERSION.RELEASE,
        run = ooExporterConfiguration.run ?: DEFAULT_RUN_NAME,
        scenario = ooExporterConfiguration.scenario,
        applicationId = ooExporterConfiguration.applicationId ?: DEFAULT_APPLICATION_ID,
        intervalInSeconds = ooExporterConfiguration.intervalInSeconds,
        env = BuildConfig.BUILD_TYPE
    )

    override fun getAggregationTemporality(instrumentType: InstrumentType): AggregationTemporality {
        return AggregationTemporality.DELTA
    }

    override fun export(metrics: Collection<MetricData>): CompletableResultCode {
        MetricRequestBodyBuilder(benchmarkContext).build(metrics.toList()).apply {
            Log.i("LogsMetricExporter", this)
        }
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
        private const val DEFAULT_RUN_NAME = "log run"
        private const val DEFAULT_APPLICATION_ID = "unassigned application id"
    }
}
