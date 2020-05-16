package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.model.posts.Posts
import com.mkt120.bloggerable.repository.PostsRepository

class SaveAllPosts(private val postsRepository: PostsRepository) {
    fun execute(posts: List<Posts>, isDraft: Boolean) {
        postsRepository.savePosts(posts, isDraft)
    }
}