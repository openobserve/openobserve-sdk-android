/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */
package com.openobserve.android.utils.forge

import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory
import org.chromium.net.RequestFinishedInfo

internal class RequestFinishedInfoMetricsForgeFactory : ForgeryFactory<RequestFinishedInfo.Metrics> {
    override fun getForgery(forge: Forge): RequestFinishedInfo.Metrics = StubRequestFinishedInfoMetrics.from(forge)
}
