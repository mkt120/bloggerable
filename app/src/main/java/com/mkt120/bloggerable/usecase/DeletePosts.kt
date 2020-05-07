package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.ApiManager
import com.mkt120.bloggerable.repository.AccountRepository
import com.mkt120.bloggerable.repository.PostsRepository

class DeletePosts(
    private val getAccessToken: GetAccessToken,
    private val postsRepository: PostsRepository
) {
    fun execute(
        userId: String,
        blogId: String,
        postsId: String,
        listener: ApiManager.CompleteListener
    ) {
        val accessToken =
            getAccessToken.execute(userId, object : AccountRepository.OnRefreshListener {
                override fun onRefresh() {
                    execute(userId, blogId, postsId, listener)
                }
                override fun onErrorResponse(code: Int, message: String) {
                    listener.onErrorResponse(code, message)
                }
                override fun onFailed(t: Throwable) {
                    listener.onFailed(t)
                }
            })
        accessToken?.let {
            deletePosts(accessToken, blogId, postsId, listener)
        }
    }

    private fun deletePosts(
        accessToken: String,
        blogId: String,
        postsId: String,
        listener: ApiManager.CompleteListener
    ) {
        postsRepository.deletePosts(
            accessToken,
            blogId,
            postsId,
            object : ApiManager.CompleteListener {
                override fun onComplete() {
                    postsRepository.deletePosts(blogId, postsId)
                    listener.onComplete()
                }

                override fun onErrorResponse(code: Int, message: String) {
                    listener.onErrorResponse(code, message)
                }
                override fun onFailed(t: Throwable) {
                    listener.onFailed(t)
                }
            })
    }
}