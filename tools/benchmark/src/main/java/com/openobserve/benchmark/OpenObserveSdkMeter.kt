/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark

import com.openobserve.android.internal.profiler.GlobalBenchmark
import com.openobserve.benchmark.exporter.OpenObserveMetricExporter
import com.openobserve.benchmark.profiler.DDBenchmarkSdkUploads
import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.sdk.OpenTelemetrySdk
import io.opentelemetry.sdk.metrics.SdkMeterProvider
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader
import java.util.concurrent.TimeUnit

/**
 * This class is responsible for managing the measurements of SDK performance
 * for example, bytes written and bytes deleted.
 * This data will be uploaded to OpenObserve metric API.
 */
class OpenObserveSdkMeter private constructor() : OpenObserveBaseMeter {

    override fun startMeasuring() {
        // for this class this is a noop since we use only PeriodicMetricReader.
    }

    override fun stopMeasuring() {
        // for this class this is a noop since we use only PeriodicMetricReader.
    }

    companion object {

        /**
         * Creates an instance of [OpenObserveSdkMeter] with the given configuration.
         */
        fun create(ooExporterConfiguration: OpenObserveExporterConfiguration): OpenObserveSdkMeter {
            val ooExporter = OpenObserveMetricExporter(ooExporterConfiguration)
            val sdkMeterProvider = SdkMeterProvider.builder()
                .registerMetricReader(
                    PeriodicMetricReader.builder(ooExporter)
                        .setInterval(ooExporterConfiguration.intervalInSeconds, TimeUnit.SECONDS)
                        .build()
                )
                .build()
            val openTelemetry: OpenTelemetry = OpenTelemetrySdk.builder()
                .setMeterProvider(sdkMeterProvider)
                .build()
            GlobalOpenTelemetry.set(openTelemetry)
            GlobalBenchmark.register(DDBenchmarkSdkUploads())
            return OpenObserveSdkMeter()
        }
    }
}
