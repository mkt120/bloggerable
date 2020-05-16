package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.repository.LastSelectBlogIdRepository

class GetLastSelectBlogId(private val lastSelectBlogId: LastSelectBlogIdRepository) {

    fun execute(): String = lastSelectBlogId.get()

}