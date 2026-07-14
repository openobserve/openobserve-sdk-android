/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.api.feature

import androidx.annotation.AnyThread
import com.openobserve.android.api.InternalLogger
import com.openobserve.android.api.context.OpenObserveContext
import com.openobserve.android.core.internal.SdkFeature
import com.openobserve.android.core.metrics.TelemetryMetricType
import com.openobserve.android.lint.InternalApi
import java.util.concurrent.Future

/**
 * Measures the execution time for the given block and report it as a MethodCall telemetry metric.
 * @param R the type of the result of the operation
 * @param callerClass the class calling the measured method
 * @param operationName the operationName to report in the metric
 * @param samplingRate the sampling rate for the metric
 * @param operation the operation to report
 */
@InternalApi
fun <R> InternalLogger.measureMethodCallPerf(
    callerClass: Class<*>,
    operationName: String,
    samplingRate: Float = 100f,
    operation: () -> R
): R {
    val metric = startPerformanceMeasure(
        callerClass = callerClass.name,
        metric = TelemetryMetricType.MethodCalled,
        samplingRate = samplingRate,
        operationName = operationName
    )

    val result = operation()

    val isSuccessful = (result != null) && ((result !is Collection<*>) || result.isNotEmpty())
    metric?.stopAndSend(isSuccessful)

    return result
}

/**
 * Utility to read current [OpenObserveContext], asynchronously.
 * @param withFeatureContexts Feature contexts ([OpenObserveContext.featuresContext] property) to include
 * in the [OpenObserveContext] provided. The value should be the feature names as declared by [Feature.name].
 * Default is empty, meaning that no feature contexts will be included.
 *
 * Returns future that will contain [OpenObserveContext] in the state that it has at the moment of call.
 */
@AnyThread
@InternalApi
fun FeatureScope.getContextFuture(
    withFeatureContexts: Set<String> = emptySet()
): Future<OpenObserveContext?>? {
    return when (this) {
        is SdkFeature -> getContextFuture(withFeatureContexts)
        else -> null
    }
}
