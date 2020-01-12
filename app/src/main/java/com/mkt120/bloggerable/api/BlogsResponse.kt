package com.mkt120.bloggerable.api

import com.google.gson.JsonObject
import com.mkt120.bloggerable.model.Blogs

data class BlogsResponse(
    var kind: String? = null,
    var items: List<Blogs>? = null,
    var blogUserInfos: List<JsonObject>? = null
) {
    fun isEmpty(): Boolean {
        return items == null || items!!.isEmpty()
    }
}