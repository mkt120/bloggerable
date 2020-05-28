package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.repository.Repository
import io.reactivex.Completable

class RevertPosts(
    private val getAccessToken: UseCase.IGetAccessToken,
    private val postsRepository: Repository.IPostsRepository
) {
    fun execute(
        now:Long,
        userId: String,
        blogId: String,
        postsId: String
    ): Completable = getAccessToken.execute(userId)
        .flatMapCompletable { accessToken ->
            postsRepository.revertPosts(accessToken, blogId, postsId)
        }
}