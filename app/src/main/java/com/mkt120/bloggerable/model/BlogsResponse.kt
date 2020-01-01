package com.mkt120.bloggerable.model

import com.google.gson.JsonObject

data class BlogsResponse(var kind: String?, var items: List<Blogs>?, var blogUserInfos: List<JsonObject>?) {
    constructor() : this(null, null, null)

    fun isEmpty() : Boolean {
        return items == null || items!!.isEmpty()
    }
}