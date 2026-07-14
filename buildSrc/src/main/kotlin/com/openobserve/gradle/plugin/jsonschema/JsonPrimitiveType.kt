/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.gradle.plugin.jsonschema

enum class JsonPrimitiveType {
    STRING, BOOLEAN, INTEGER, NUMBER
}

fun JsonPrimitiveType.nameString(): String {
    return when (this) {
        JsonPrimitiveType.STRING -> "String"
        JsonPrimitiveType.BOOLEAN -> "Boolean"
        JsonPrimitiveType.INTEGER -> "Long"
        JsonPrimitiveType.NUMBER -> "Number"
    }
}
