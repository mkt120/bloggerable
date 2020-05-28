package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.repository.Repository
import io.reactivex.Completable

class DeletePosts(
    private val getAccessToken: UseCase.IGetAccessToken,
    private val postsRepository: Repository.IPostsRepository
) {
    fun execute(
        userId: String,
        blogId: String,
        postsId: String
    ): Completable {
        return getAccessToken.execute(userId)
            .flatMapCompletable { accessToken ->
                postsRepository.deletePosts(
                    accessToken,
                    blogId,
                    postsId
                )
            }.andThen(postsRepository.deletePosts(blogId, postsId))
    }
}