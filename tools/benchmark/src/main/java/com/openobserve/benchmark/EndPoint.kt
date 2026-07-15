/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.openobserve.benchmark

/**
 * Enumeration of datadog metrics, traces endpoints.
 */
enum class EndPoint(
    private val metrics: String,
    private val traces: String
) {

    US1(
        metrics = "https://api.openobserve.ai/",
        traces = "https://browser-intake-openobserve.ai/"
    ),
    US3(
        metrics = "https://api.us3.openobserve.ai/",
        traces = "https://browser-intake-us3-openobserve.ai/"
    ),
    US5(
        metrics = "https://api.us5.openobserve.ai/",
        traces = "https://browser-intake-us5-openobserve.ai/"
    ),
    EU1(
        metrics = "https://api.openobserve.eu/",
        traces = "https://public-trace-http-intake.logs.openobserve.eu/"
    ),
    AP1(
        metrics = "https://api.ap1.openobserve.ai/",
        traces = "https://browser-intake-ap1-openobserve.ai/"
    ),
    AP2(
        metrics = "https://api.ap2.openobserve.ai/",
        traces = "https://browser-intake-ap2-openobserve.ai/"
    );

    /**
     * Gets the url for submitting metrics.
     */
    fun metricUrl(): String = metrics + "api/v2/series"

    /**
     * Gets the url for submitting traces.
     */
    fun tracesUrl(): String = traces + "api/v2/spans"
}
