package com.mkt120.bloggerable.model.blogs

import io.realm.RealmObject

open class Locale(
    var language: String? = null,
    var country: String? = null,
    var variant: String? = null
) : RealmObject()

