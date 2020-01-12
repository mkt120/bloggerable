package com.mkt120.bloggerable.api

import com.mkt120.bloggerable.model.Posts

data class PostsResponse(
    var kind: String? = null,
    var nextPageToken: String? = null,
    var items: List<Posts>? = null
)