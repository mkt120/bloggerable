package com.mkt120.bloggerable.model

data class PostsResponse(
    var kind: String? = null,
    var nextPageToken: String? = null,
    var items: List<Posts>? = null
)