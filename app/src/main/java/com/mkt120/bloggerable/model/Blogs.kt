package com.mkt120.bloggerable.model

import android.os.Parcel
import android.os.Parcelable
import java.text.SimpleDateFormat
import java.util.*

data class Blogs(
    var kind: String? = null,
    var id: String? = null,
    var name: String? = null,
    var description: String? = null,
    var published: String? = null,
    var updated: String? = null,
    var url: String? = null,
    var selfLink: String? = null,
    var posts: Posts? = null,
    var pages: Pages? = null,
    var locale: Locale? = null
) : Parcelable {

    data class Posts(
        var totalItems: String? = null,
        var selfLink: String? = null
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString()
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(totalItems)
            parcel.writeString(selfLink)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Posts> {
            override fun createFromParcel(parcel: Parcel): Posts {
                return Posts(parcel)
            }

            override fun newArray(size: Int): Array<Posts?> {
                return arrayOfNulls(size)
            }
        }
    }

    data class Pages(
        var totalItems: String? = null,
        var selfLink: String? = null
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString()
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(totalItems)
            parcel.writeString(selfLink)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Pages> {
            override fun createFromParcel(parcel: Parcel): Pages {
                return Pages(parcel)
            }

            override fun newArray(size: Int): Array<Pages?> {
                return arrayOfNulls(size)
            }
        }
    }

    data class Locale(
        var language: String? = null,
        var country: String? = null,
        var variant: String? = null
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString()
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(language)
            parcel.writeString(country)
            parcel.writeString(variant)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Locale> {
            override fun createFromParcel(parcel: Parcel): Locale {
                return Locale(parcel)
            }

            override fun newArray(size: Int): Array<Locale?> {
                return arrayOfNulls(size)
            }
        }
    }

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Posts::class.java.classLoader),
        parcel.readParcelable(Pages::class.java.classLoader),
        parcel.readParcelable(Locale::class.java.classLoader)
    )

    fun getPublishDate(): Date {
        return getDate(published!!)
    }

    fun getLastUpdate(): Date {
        return getDate(updated!!)
    }

    fun getDate(date: String) : Date {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", java.util.Locale.JAPAN)
        return simpleDateFormat.parse(date)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(kind)
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(published)
        parcel.writeString(updated)
        parcel.writeString(url)
        parcel.writeString(selfLink)
        parcel.writeParcelable(posts, flags)
        parcel.writeParcelable(pages, flags)
        parcel.writeParcelable(locale, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Blogs> {
        override fun createFromParcel(parcel: Parcel): Blogs {
            return Blogs(parcel)
        }

        override fun newArray(size: Int): Array<Blogs?> {
            return arrayOfNulls(size)
        }
    }

}