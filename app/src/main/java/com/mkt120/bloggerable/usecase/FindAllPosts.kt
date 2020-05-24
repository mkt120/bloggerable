package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.model.posts.Posts
import com.mkt120.bloggerable.repository.Repository
import io.reactivex.Single

class FindAllPosts(private val postsRepository: Repository.IPostsRepository) {
    fun execute(blogId: String?, isPost: Boolean): Single<List<Posts>> =
        postsRepository.findAllPosts(blogId, isPost)
}