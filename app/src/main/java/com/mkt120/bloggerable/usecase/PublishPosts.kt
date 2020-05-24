package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.repository.Repository

class PublishPosts(
    private val getAccessToken: UseCase.IGetAccessToken,
    private val postsRepository: Repository.IPostsRepository
) {
    fun execute(
        userId: String,
        blogsId: String,
        postsId: String,
        onComplete: () -> Unit,
        onFailed: (Throwable) -> Unit
    ) {
        getAccessToken.execute(userId, System.currentTimeMillis()).flatMapCompletable { accessToken ->
            postsRepository.publishPosts(accessToken, blogsId, postsId)
        }.subscribe(onComplete, onFailed)
    }
}