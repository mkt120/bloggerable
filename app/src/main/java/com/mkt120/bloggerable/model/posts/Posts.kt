package com.mkt120.bloggerable.model.posts

import androidx.core.text.HtmlCompat
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

open class Posts(
    var kind: String? = null,
    @PrimaryKey var id: String? = null,
    var blog: Blog? = null,
    var url: String? = null,
    var published: String? = null,
    var updated: String? = null,
    var selfLink: String? = null,
    var title: String? = null,
    var content: String? = null,
    var replies: Reply? = null,
    var isPost: Boolean = false,
    var labels: RealmList<String>? = null
) : RealmObject() {

    fun isChange(title: String, html: String): Boolean {
        val changeTitle = this.title != title
        val fromHtml = HtmlCompat.fromHtml(content!!, HtmlCompat.FROM_HTML_MODE_LEGACY)
        val toHtml = HtmlCompat.toHtml(fromHtml, HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE)
        val changeContent = toHtml != html
        return changeTitle || changeContent
    }

    private fun getDate(): Date {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.JAPAN)
        return format.parse(published)
    }

    fun getStringDate(): String {
        val format = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.JAPAN)
        return format.format(getDate())
    }

    companion object {
        fun createPosts(
            title: String,
            content: String,
            labels: Array<String>?
        ): HashMap<String, Any> =
            HashMap<String, Any>().apply {
                this["title"] = title
                this["content"] = content
                if (labels != null) {
                    this["labels"] = labels
                }
            }
    }
}