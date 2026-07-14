/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.benchmark.forge

import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.sdk.metrics.data.PointData
import io.opentelemetry.sdk.metrics.internal.data.ImmutableDoublePointData

class PointDataForgeryFactory : ForgeryFactory<PointData> {
    override fun getForgery(forge: Forge): PointData {
        return ImmutableDoublePointData.create(
            forge.aLong(),
            forge.aLong(),
            Attributes.empty(),
            forge.aDouble()
        )
    }
}
