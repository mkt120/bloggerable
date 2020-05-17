package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.repository.PostsRepository

class RevertPosts(
    private val getAccessToken: GetAccessToken,
    private val postsRepository: PostsRepository
) {
    fun execute(
        userId: String,
        blogId: String,
        postsId: String,
        onComplete: () -> Unit,
        onFailed: (Throwable) -> Unit
    ) {
        getAccessToken.execute(userId).flatMapCompletable { accessToken ->
            postsRepository.revertPosts(accessToken, blogId, postsId)
        }.subscribe(onComplete, onFailed)
    }
}