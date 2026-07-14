/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.tools.detekt.ext

import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import org.jetbrains.kotlin.psi.psiUtil.isPublic
import org.jetbrains.kotlin.util.containingNonLocalDeclaration

internal fun KtExpression.isContainingClassPublic(): Boolean {
    return containingClass().let { ktClass ->
        ktClass == null || ktClass.isPublic
    }
}

internal fun KtExpression.isContainingDeclarationPublic(): Boolean {
    return containingNonLocalDeclaration().let { ktDeclaration ->
        ktDeclaration == null || ktDeclaration.isPublic
    }
}

internal fun KtExpression.isContainingEntryPointPublic(): Boolean {
    return isContainingClassPublic() && isContainingDeclarationPublic()
}
