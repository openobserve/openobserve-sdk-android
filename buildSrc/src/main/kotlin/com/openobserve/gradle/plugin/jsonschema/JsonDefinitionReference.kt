/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.gradle.plugin.jsonschema

import java.io.File

data class JsonDefinitionReference(
    val typeName: String,
    val definition: JsonDefinition,
    val fromFile: File
)
