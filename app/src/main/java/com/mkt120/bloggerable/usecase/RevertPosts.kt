package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.ApiManager
import com.mkt120.bloggerable.repository.AccessTokenRepository
import com.mkt120.bloggerable.repository.PostsRepository

class RevertPosts(
    private val getAccessToken: GetAccessToken,
    private val postsRepository: PostsRepository
) {
    fun execute(blogId: String, postsId: String, listener: ApiManager.CompleteListener) {
        val accessToken = getAccessToken.execute(object : AccessTokenRepository.OnRefreshListener {
            override fun onRefresh() {
                execute(blogId, postsId, listener)
            }
        })
        if (accessToken != null) {
            revertPosts(accessToken, blogId, postsId, listener)
        }
    }

    private fun revertPosts(
        accessToken: String,
        blogId: String,
        postsId: String,
        listener: ApiManager.CompleteListener
    ) {
        postsRepository.revertPosts(accessToken, blogId, postsId, listener)
    }
}