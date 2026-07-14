/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.log.internal.domain.event

import com.openobserve.android.api.InternalLogger
import com.openobserve.android.core.constraints.DataConstraints
import com.openobserve.android.core.constraints.OpenObserveDataConstraints
import com.openobserve.android.core.internal.utils.JsonSerializer.safeMapValuesToJson
import com.openobserve.android.core.persistence.Serializer
import com.openobserve.android.log.LogAttributes
import com.openobserve.android.log.model.LogEvent

internal class LogEventSerializer(
    private val internalLogger: InternalLogger,
    private val dataConstraints: DataConstraints = OpenObserveDataConstraints(internalLogger)
) : Serializer<LogEvent> {

    override fun serialize(model: LogEvent): String {
        return sanitizeTagsAndAttributes(model).toJson().toString()
    }

    private fun sanitizeTagsAndAttributes(log: LogEvent): LogEvent {
        val sanitizedTags = dataConstraints
            .validateTags(log.ddtags.split(","))
            .joinToString(",")
        val sanitizedAttributes = dataConstraints
            .validateAttributes(log.additionalProperties)
            .filterKeys { it.isNotBlank() }
        val usr = log.usr?.let {
            val sanitizedUserAttributes = dataConstraints.validateAttributes(
                it.additionalProperties,
                keyPrefix = LogAttributes.USR_ATTRIBUTES_GROUP,
                attributesGroupName = USER_EXTRA_GROUP_VERBOSE_NAME
            )
            it.copy(
                additionalProperties = sanitizedUserAttributes
                    .safeMapValuesToJson(internalLogger)
                    .toMutableMap()
            )
        }
        val account = log.account?.let {
            val sanitizedAccountAttributes = dataConstraints.validateAttributes(
                it.additionalProperties,
                keyPrefix = LogAttributes.ACCOUNT_ATTRIBUTES_GROUP,
                attributesGroupName = ACCOUNT_EXTRA_GROUP_VERBOSE_NAME
            )
            it.copy(
                additionalProperties = sanitizedAccountAttributes
                    .safeMapValuesToJson(internalLogger)
                    .toMutableMap()
            )
        }
        return log.copy(
            ddtags = sanitizedTags,
            additionalProperties = sanitizedAttributes
                .safeMapValuesToJson(internalLogger)
                .toMutableMap(),
            usr = usr,
            account = account
        )
    }

    companion object {
        internal const val USER_EXTRA_GROUP_VERBOSE_NAME = "user extra information"
        internal const val ACCOUNT_EXTRA_GROUP_VERBOSE_NAME = "account extra information"
    }
}
