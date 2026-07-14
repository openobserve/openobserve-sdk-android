/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.trace.common.sampling

import org.junit.jupiter.api.io.TempDir
import java.io.File

internal class SpanSamplingRulesFileTest : SpanSamplingRulesTest() {

    @TempDir
    lateinit var tempDir: File

    override fun deserializeRules(jsonRules: String): SpanSamplingRules {
        return SpanSamplingRules.deserializeFile(createRulesFile(jsonRules))
    }

    fun createRulesFile(rules: String): String {
        val p = File(tempDir, "single-span-sampling-rules.json")
        p.createNewFile()
        p.writeText(rules)
        return p.toString()
    }
}
