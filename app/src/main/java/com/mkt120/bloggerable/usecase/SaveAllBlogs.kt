package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.repository.BlogRepository

class SaveAllBlogs(private val repository: BlogRepository) {

    fun execute(blogs: List<Blogs>?) {
        if (blogs != null) {
            repository.saveAllBlog(blogs)
        }
    }
}
