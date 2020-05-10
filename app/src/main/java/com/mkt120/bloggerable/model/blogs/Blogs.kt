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
    var posts: Post? = null,
    var pages: Pages? = null,
    var locale: Locale? = null,
    var lastRequestPosts: Long = 0
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

    fun isExpired(now: Long): Boolean = now - lastRequestPosts >= 24 * 60 * 60 * 1000


    fun updateLastRequest(update: Long) {
        lastRequestPosts = update
    }

}