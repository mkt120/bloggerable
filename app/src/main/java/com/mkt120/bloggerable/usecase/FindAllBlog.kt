package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.repository.Repository
import io.reactivex.Single

class FindAllBlog(private val blogRepository: Repository.IBlogRepository) {
    fun execute(userId: String): Single<MutableList<Blogs>> = blogRepository.findAllBlog(userId)
}