package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.repository.PostsRepository

class PublishPosts(
    private val getAccessToken: GetAccessToken,
    private val postsRepository: PostsRepository
) {
    fun execute(
        userId: String,
        blogsId: String,
        postsId: String,
        onComplete: () -> Unit,
        onFailed: (Throwable) -> Unit
    ) {
        getAccessToken.execute(userId).flatMapCompletable { accessToken ->
            postsRepository.publishPosts(accessToken, blogsId, postsId)
        }.subscribe(onComplete, onFailed)
    }
}