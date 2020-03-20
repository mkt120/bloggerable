package com.mkt120.bloggerable.api

import com.mkt120.bloggerable.model.blogs.Blogs

data class BlogsResponse(
    var kind: String? = null,
    var items: List<Blogs>? = null
) {
}