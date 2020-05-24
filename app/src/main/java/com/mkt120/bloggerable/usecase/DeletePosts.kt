package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.repository.Repository

class DeletePosts(
    private val getAccessToken: UseCase.IGetAccessToken,
    private val postsRepository: Repository.IPostsRepository
) {
    fun execute(
        userId: String,
        blogId: String,
        postsId: String,
        onComplete: () -> Unit,
        onFailed: (Throwable) -> Unit
    ) {
        getAccessToken.execute(userId, System.currentTimeMillis()).flatMapCompletable { accessToken ->
            postsRepository.deletePosts(
                accessToken,
                blogId,
                postsId
            )
        }.subscribe({
            postsRepository.deletePosts(blogId, postsId)
            onComplete()
        }, onFailed)
    }
}