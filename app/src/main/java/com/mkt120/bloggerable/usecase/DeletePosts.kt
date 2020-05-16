package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.ApiManager
import com.mkt120.bloggerable.repository.AccessTokenRepository
import com.mkt120.bloggerable.repository.PostsRepository

class DeletePosts(
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
            deletePosts(accessToken, blogId, postsId, listener)
        }
    }

    private fun deletePosts(
        accessToken: String,
        blogId: String,
        postsId: String,
        listener: ApiManager.CompleteListener
    ) {
        postsRepository.deletePosts(accessToken, blogId, postsId, listener)
    }
}