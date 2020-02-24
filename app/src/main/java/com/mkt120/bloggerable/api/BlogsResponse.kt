package com.mkt120.bloggerable.api

import android.os.Parcel
import android.os.Parcelable
import com.mkt120.bloggerable.model.Blogs

data class BlogsResponse(
    var kind: String? = null,
    var items: List<Blogs>? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.createTypedArrayList(Blogs)
    ) {
    }

    fun isEmpty(): Boolean {
        return items == null || items!!.isEmpty()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(kind)
        parcel.writeTypedList(items)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BlogsResponse> {
        override fun createFromParcel(parcel: Parcel): BlogsResponse {
            return BlogsResponse(parcel)
        }

        override fun newArray(size: Int): Array<BlogsResponse?> {
            return arrayOfNulls(size)
        }
    }


}