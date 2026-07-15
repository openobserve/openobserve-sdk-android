/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */
package com.openobserve.android.utils.forge

import com.openobserve.android.trace.api.trace.OpenObserveTraceId
import com.openobserve.android.trace.internal.OpenObserveTraceIdAdapter
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

class OpenObserveTraceIdForgeryFactory : ForgeryFactory<OpenObserveTraceId> {
    override fun getForgery(forge: Forge): OpenObserveTraceId {
        return OpenObserveTraceIdAdapter(forge.getForgery())
    }
}
