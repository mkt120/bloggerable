package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.repository.LastSelectBlogIdRepository

class SaveLastSelectBlogId(private val lastSelectBlogId: LastSelectBlogIdRepository) {

    fun execute(blogId: String) {
        lastSelectBlogId.save(blogId)
    }
}