package com.mkt120.bloggerable.model.blogs

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

open class Blogs(
    var kind: String? = null,
    @PrimaryKey var id: String? = null,
    var name: String? = null,
    var description: String? = null,
    var published: String? = null,
    var updated: String? = null,
    var url: String? = null,
    var selfLink: String? = null,
    var post: Post? = null,
    var pages: Pages? = null,
    var locale: Locale? = null
) : RealmObject() {

    fun getPublishDate(): Date {
        return getDate(published!!)
    }

    fun getLastUpdate(): Date {
        return getDate(updated!!)
    }

    private fun getDate(date: String): Date {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", java.util.Locale.JAPAN)
        return simpleDateFormat.parse(date)
    }

}