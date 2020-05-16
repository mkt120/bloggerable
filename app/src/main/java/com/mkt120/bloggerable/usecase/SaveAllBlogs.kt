package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.repository.BlogsRepository

class SaveAllBlogs(private val repository: BlogsRepository) {

    fun execute(blogs: List<Blogs>?) {
        if (blogs != null) {
            repository.saveAllBlogs(blogs)
        }
    }
}
