/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.gradle.plugin.jsonschema

import com.squareup.kotlinpoet.TypeName

data class DefinitionRef(
    val definition: JsonDefinition,
    val id: String,
    val typeName: TypeName,
    val className: String
)
