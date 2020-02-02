package com.mkt120.bloggerable.api

import android.os.Parcel
import android.os.Parcelable
import com.mkt120.bloggerable.model.Posts

data class PostsResponse(
    var kind: String? = null,
    var nextPageToken: String? = null,
    var items: List<Posts>? = null
) : Parcelable {

    fun createLabelList(): MutableList<String> {
        val set: MutableSet<String> = mutableSetOf()
        if (items == null) {
            return mutableListOf()
        }
        for (item in items!!) {
            item.labels?.let {
                set.addAll(it)
            }
        }
        return set.toMutableList()
    }

    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.createTypedArrayList(Posts.CREATOR)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(kind)
        writeString(nextPageToken)
        writeTypedList(items)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<PostsResponse> =
            object : Parcelable.Creator<PostsResponse> {
                override fun createFromParcel(source: Parcel): PostsResponse = PostsResponse(source)
                override fun newArray(size: Int): Array<PostsResponse?> = arrayOfNulls(size)
            }
    }
}