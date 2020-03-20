package com.mkt120.bloggerable.model.posts

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Blog(@PrimaryKey var id: String? = null) : RealmObject()