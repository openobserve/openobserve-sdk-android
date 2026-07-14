/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.gradle.plugin.gitclone

import java.io.Serializable

open class GitCloneDependenciesExtension : Serializable {

    internal data class Dependency(
        var originRepository: String,
        var originSubFolder: String,
        var excludedPrefixes: List<String>,
        var originRef: String,
        var destinationFolder: String
    ) : Serializable {
        companion object {
            private const val serialVersionUID: Long = 1L
        }
    }

    internal val dependencies: MutableList<Dependency> = mutableListOf()

    fun clone(
        repo: String,
        subFolder: String = "",
        ref: String = "master",
        excludedPrefixes: List<String> = emptyList(),
        destinationFolder: String = ""
    ) {
        dependencies.add(
            Dependency(repo, subFolder, excludedPrefixes, ref, destinationFolder)
        )
    }

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
