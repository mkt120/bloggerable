package com.mkt120.bloggerable.model.blogs

import io.realm.RealmObject

open class Post(
    var totalItems: Int? = null,
    var selfLink: String? = null
) : RealmObject()