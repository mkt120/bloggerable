package com.mkt120.bloggerable.model.blogs

import io.realm.RealmObject

open class Pages(
    var totalItems: String? = null,
    var selfLink: String? = null
) : RealmObject()

