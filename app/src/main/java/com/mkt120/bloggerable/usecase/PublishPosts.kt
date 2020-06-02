package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.repository.Repository
import io.reactivex.Completable

class PublishPosts(
    private val getAccessToken: UseCase.IGetAccessToken,
    private val postsRepository: Repository.IPostsRepository
) {
    fun execute(
        now: Long,
        userId: String,
        blogsId: String,
        postsId: String
    ): Completable = getAccessToken.execute(userId).flatMapCompletable { accessToken ->
        postsRepository.publishPosts(accessToken, blogsId, postsId)
    }
}