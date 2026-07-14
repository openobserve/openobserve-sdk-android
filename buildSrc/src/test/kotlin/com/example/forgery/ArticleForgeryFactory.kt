/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.example.forgery
import com.example.model.Article
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class ArticleForgeryFactory : ForgeryFactory<Article> {
    override fun getForgery(forge: Forge): Article {
        return Article(
            title = forge.anAlphabeticalString(),
            authors = forge.aList { anAlphabeticalString() },
            tags = forge.aNullable { aList { anAlphabeticalString() } }
        )
    }
}
