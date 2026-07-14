/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.internal.profiler

import com.openobserve.android.internal.time.TimeProvider

internal class DDExecutionTimer(
    private val track: String,
    private val timeProvider: TimeProvider,
    private val benchmarkSdkUploads: BenchmarkSdkUploads = GlobalBenchmark.getBenchmarkSdkUploads()
) : ExecutionTimer {

    override fun <T> measure(action: () -> T): T {
        if (track.isEmpty()) {
            return action()
        }

        val requestStartTime = timeProvider.getDeviceElapsedTimeNanos()
        val result = action()
        val latencyInSeconds = (timeProvider.getDeviceElapsedTimeNanos() - requestStartTime) / NANOSECONDS_IN_A_SECOND
        responseLatencyReport(latencyInSeconds, track)
        return result
    }

    private fun responseLatencyReport(latencySeconds: Double, track: String) {
        val tags = mapOf(
            TRACK_NAME to track
        )

        benchmarkSdkUploads
            .getMeter(METER_NAME)
            .createObservableGauge(BENCHMARK_RESPONSE_LATENCY, tags) {
                latencySeconds
            }
    }

    private companion object {
        private const val TRACK_NAME = "track"
        private const val METER_NAME = "oo-sdk-android"
        private const val BENCHMARK_RESPONSE_LATENCY = "android.benchmark.response_latency"
        private const val NANOSECONDS_IN_A_SECOND = 1_000_000_000.0
    }
}
