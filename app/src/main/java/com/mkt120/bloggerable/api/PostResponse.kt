package com.mkt120.bloggerable.api

import com.mkt120.bloggerable.model.posts.Posts

data class PostResponse(
    var items: List<Posts>? = null
)