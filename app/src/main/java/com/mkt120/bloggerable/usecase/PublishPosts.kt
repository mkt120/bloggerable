package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.ApiManager
import com.mkt120.bloggerable.repository.AccessTokenRepository
import com.mkt120.bloggerable.repository.PostsRepository

class PublishPosts(
    private val getAccessToken: GetAccessToken,
    private val postsRepository: PostsRepository
) {
    fun execute(blogsId: String, postsId: String, listener: ApiManager.CompleteListener) {
        val accessToken = getAccessToken.execute(object : AccessTokenRepository.OnRefreshListener {
            override fun onRefresh() {
                execute(blogsId, postsId, listener)
            }
        })
        if (accessToken != null) {
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