package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.ApiManager
import com.mkt120.bloggerable.repository.AccountRepository
import com.mkt120.bloggerable.repository.PostsRepository

class CreatePosts(
    private val getAccessToken: GetAccessToken,
    private val postsRepository: PostsRepository
) {
    fun execute(
        userId: String,
        blogId: String,
        title: String,
        html: String,
        labels: Array<String>?,
        draft: Boolean,
        completeListener: ApiManager.CompleteListener
    ) {
        val accessToken =
            getAccessToken.execute(userId, object : AccountRepository.OnRefreshListener {
                override fun onRefresh() {
                    execute(userId, blogId, title, html, labels, draft, completeListener)
                }
                override fun onErrorResponse(code: Int, message: String) {
                    completeListener.onErrorResponse(code, message)
                }
                override fun onFailed(t: Throwable) {
                    completeListener.onFailed(t)
                }
            })
        accessToken?.let {
            createPost(accessToken, blogId, title, html, labels, draft, completeListener)
        }
    }

    private fun createPost(
        accessToken: String,
        blogId: String,
        title: String,
        html: String,
        labels: Array<String>?,
        draft: Boolean,
        completeListener: ApiManager.CompleteListener
    ) {
        postsRepository.createPosts(
            accessToken,
            blogId,
            title,
            html,
            labels,
            draft,
            completeListener
        )
    }
}