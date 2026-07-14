/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.internal.network

/**
 * Headers used internally for intercepting GraphQL requests.
 * @param headerValue name of the header.
 */
// TODO RUM-13454 - move these headers to [HttpSpec]
enum class GraphQLHeaders(val headerValue: String) {
    DD_GRAPHQL_NAME_HEADER("_oo-custom-header-graph-ql-operation-name"),
    DD_GRAPHQL_VARIABLES_HEADER("_oo-custom-header-graph-ql-variables"),
    DD_GRAPHQL_TYPE_HEADER("_oo-custom-header-graph-ql-operation-type"),
    DD_GRAPHQL_PAYLOAD_HEADER("_oo-custom-header-graph-ql-payload")
}
