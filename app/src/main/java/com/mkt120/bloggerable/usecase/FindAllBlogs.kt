package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.repository.BlogsRepository

class FindAllBlogs(private val blogsRepository: BlogsRepository) {
    fun execute(): List<Blogs> = blogsRepository.findAllBlogs()
}