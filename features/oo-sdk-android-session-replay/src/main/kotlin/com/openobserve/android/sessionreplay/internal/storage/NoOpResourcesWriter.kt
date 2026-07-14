/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.sessionreplay.internal.storage

import com.openobserve.android.sessionreplay.internal.processor.EnrichedResource

internal class NoOpResourcesWriter : ResourcesWriter {
    override fun write(enrichedResource: EnrichedResource) {
        // no-op
    }
}
