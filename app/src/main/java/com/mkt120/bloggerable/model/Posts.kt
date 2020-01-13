package com.mkt120.bloggerable.model

import android.os.Parcel
import android.os.Parcelable

data class Posts(
    var kind: String? = null,
    var id: String? = null,
    var url: String? = null,
    var published: String? = null,
    var updated: String? = null,
    var selfLink: String? = null,
    var title: String? = null,
    var content: String? = null
) : Parcelable {

    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(kind)
        writeString(id)
        writeString(url)
        writeString(published)
        writeString(updated)
        writeString(selfLink)
        writeString(title)
        writeString(content)
    }

    companion object {
        public fun createPosts(title: String, content: String): HashMap<String, String> =
            HashMap<String, String>().apply {
                this["title"] = title
                this["content"] = content
            }

        @JvmField
        val CREATOR: Parcelable.Creator<Posts> = object : Parcelable.Creator<Posts> {
            override fun createFromParcel(source: Parcel): Posts = Posts(source)
            override fun newArray(size: Int): Array<Posts?> = arrayOfNulls(size)
        }
    }
}