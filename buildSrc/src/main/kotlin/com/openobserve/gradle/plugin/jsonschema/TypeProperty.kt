/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.gradle.plugin.jsonschema

data class TypeProperty(
    val name: String,
    val type: TypeDefinition,
    val readOnly: Boolean = true,
    val defaultValue: Any? = null
) {
    fun mergedWith(other: TypeProperty): TypeProperty {
        return if (this == other) {
            this
        } else {
            TypeProperty(
                name = name,
                type = type.mergedWith(other.type)
            )
        }
    }
}
