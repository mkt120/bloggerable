package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.model.posts.Posts
import com.mkt120.bloggerable.repository.PostsRepository

class FindAllPosts(private val postsRepository: PostsRepository) {
    fun execute(blogId: String, isPost:Boolean): List<Posts> = postsRepository.findAllPosts(blogId, isPost)
}