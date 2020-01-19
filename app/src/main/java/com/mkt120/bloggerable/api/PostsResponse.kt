package com.mkt120.bloggerable.api

import com.mkt120.bloggerable.model.Posts

data class PostsResponse(
    var kind: String? = null,
    var nextPageToken: String? = null,
    var items: List<Posts>? = null
) {

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
}