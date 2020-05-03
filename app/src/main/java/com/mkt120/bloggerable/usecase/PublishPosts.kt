package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.ApiManager
import com.mkt120.bloggerable.repository.AccountRepository
import com.mkt120.bloggerable.repository.PostsRepository

class PublishPosts(
    private val getAccessToken: GetAccessToken,
    private val postsRepository: PostsRepository
) {
    fun execute(userId:String, blogsId: String, postsId: String, listener: ApiManager.CompleteListener) {
        val accessToken = getAccessToken.execute(userId, object : AccountRepository.OnRefreshListener {
            override fun onRefresh() {
                execute(userId, blogsId, postsId, listener)
            }
            override fun onErrorResponse(code: Int, message: String) {
                listener.onErrorResponse(code, message)
            }
            override fun onFailed(t: Throwable) {
                listener.onFailed(t)
            }
        })
        accessToken?.let {
            publishPosts(accessToken, blogsId, postsId, listener)
        }
    }

    private fun publishPosts(
        accessToken: String,
        blogsId: String,
        postsId: String,
        listener: ApiManager.CompleteListener
    ) {
        postsRepository.publishPosts(accessToken, blogsId, postsId, listener)
    }
}