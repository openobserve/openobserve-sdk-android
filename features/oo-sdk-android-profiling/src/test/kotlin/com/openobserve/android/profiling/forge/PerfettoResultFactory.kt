/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.profiling.forge

import com.openobserve.android.profiling.internal.perfetto.PerfettoResult
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class PerfettoResultFactory : ForgeryFactory<PerfettoResult> {
    override fun getForgery(forge: Forge): PerfettoResult {
        return PerfettoResult(
            start = forge.aLong(),
            end = forge.aLong(),
            tag = forge.aString(),
            resultFilePath = forge.anAlphabeticalString()
        )
    }
}
