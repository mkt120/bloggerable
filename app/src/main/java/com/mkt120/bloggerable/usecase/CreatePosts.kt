package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.repository.Repository
import io.reactivex.Completable

class CreatePosts(
    private val getAccessToken: UseCase.IGetAccessToken,
    private val postsRepository: Repository.IPostsRepository
) {
    fun execute(
        userId: String,
        blogId: String,
        title: String,
        html: String,
        labels: Array<String>?,
        draft: Boolean,
        onComplete: () -> Unit,
        onError: (Throwable) -> Unit
    ) {

        getAccessToken.execute(userId, System.currentTimeMillis()).flatMapCompletable { accessToken ->
            createPost(accessToken, blogId, title, html, labels, draft)
        }.subscribe(onComplete, onError)
    }

    private fun createPost(
        accessToken: String,
        blogId: String,
        title: String,
        html: String,
        labels: Array<String>?,
        draft: Boolean
    ): Completable {
        return postsRepository.createPosts(
            accessToken,
            blogId,
            title,
            html,
            labels,
            draft
        )
    }
}