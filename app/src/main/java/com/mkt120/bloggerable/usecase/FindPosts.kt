package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.model.posts.Posts
import com.mkt120.bloggerable.repository.Repository

class FindPosts(private val postsRepository: Repository.IPostsRepository) {
    fun execute(blogId: String, postsId: String): Posts? =
        postsRepository.findPosts(blogId, postsId)
}
