package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.repository.BlogRepository

class FindAllBlog(private val blogRepository: BlogRepository) {
    fun execute(userId:String): List<Blogs> = blogRepository.findAllBlog(userId)
}