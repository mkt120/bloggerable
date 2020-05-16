package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.model.posts.Posts
import com.mkt120.bloggerable.repository.PostsRepository

class FindPosts(private val postsRepository: PostsRepository) {
    fun execute(blogId: String, postsId: String): Posts? =
        postsRepository.findPosts(blogId, postsId)
}
