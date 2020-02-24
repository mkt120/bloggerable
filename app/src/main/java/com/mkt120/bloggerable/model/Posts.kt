package com.mkt120.bloggerable.model

import android.os.Parcel
import android.os.Parcelable
import android.text.Html
import java.text.SimpleDateFormat
import java.util.*

data class Posts(
    var kind: String? = null,
    var id: String? = null,
    var blog: Blog? = null,
    var url: String? = null,
    var published: String? = null,
    var updated: String? = null,
    var selfLink: String? = null,
    var title: String? = null,
    var content: String? = null,
    var replies: Reply? = null,
    var labels: Array<String>? = null
) : Parcelable {

    data class Blog(var id: String? = null) : Parcelable {
        constructor(source: Parcel) : this(
            source.readString()
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
            writeString(id)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<Blog> = object : Parcelable.Creator<Blog> {
                override fun createFromParcel(source: Parcel): Blog = Blog(source)
                override fun newArray(size: Int): Array<Blog?> = arrayOfNulls(size)
            }
        }
    }

    data class Reply(var totalItems: Long? = null) : Parcelable {
        constructor(source: Parcel) : this(
            source.readValue(Long::class.java.classLoader) as Long?
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
            writeValue(totalItems)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<Reply> = object : Parcelable.Creator<Reply> {
                override fun createFromParcel(source: Parcel): Reply = Reply(source)
                override fun newArray(size: Int): Array<Reply?> = arrayOfNulls(size)
            }
        }
    }

    fun isChange(title: String, content: String): Boolean {
        val changeTitle = this.title != title
        // todo:改善余地あり
        val changeContent =
            Html.fromHtml(this.content, Html.FROM_HTML_MODE_COMPACT).toString() != content

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

    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readParcelable<Blog>(Blog::class.java.classLoader),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readParcelable<Reply>(Reply::class.java.classLoader),
        source.createStringArray()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(kind)
        writeString(id)
        writeParcelable(blog, 0)
        writeString(url)
        writeString(published)
        writeString(updated)
        writeString(selfLink)
        writeString(title)
        writeString(content)
        writeParcelable(replies, 0)
        writeStringArray(labels)
    }

    companion object {
        public fun createPosts(
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

        @JvmField
        val CREATOR: Parcelable.Creator<Posts> = object : Parcelable.Creator<Posts> {
            override fun createFromParcel(source: Parcel): Posts = Posts(source)
            override fun newArray(size: Int): Array<Posts?> = arrayOfNulls(size)
        }
    }
}