/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.android.utils.forge

import com.openobserve.android.core.internal.data.upload.UploadStatus
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class InvalidTokenErrorStatusForgeryFactory : ForgeryFactory<UploadStatus.InvalidTokenError> {

    override fun getForgery(forge: Forge): UploadStatus.InvalidTokenError {
        return UploadStatus.InvalidTokenError(responseCode = forge.aPositiveInt())
    }
}
