package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.ApiManager
import com.mkt120.bloggerable.repository.AccessTokenRepository
import com.mkt120.bloggerable.repository.PostsRepository

class RequestAllPosts(
    private val getAccessToken: GetAccessToken,
    private val postsRepository: PostsRepository
) {
    fun execute(isDraft: Boolean, blogId: String, postsListener: ApiManager.PostsListener) {
        val accessToken = getAccessToken.execute(object : AccessTokenRepository.OnRefreshListener {
            override fun onRefresh() {
                execute(isDraft, blogId, postsListener)
            }
        })
        if (accessToken != null) {
            if (isDraft) {
                getDraftPosts(accessToken, blogId, postsListener)
            } else {
                getLivePosts(accessToken, blogId, postsListener)
            }
        }
    }

    private fun getLivePosts(
        accessToken: String,
        blogId: String,
        postsListener: ApiManager.PostsListener
    ) {
        postsRepository.requestLivePosts(accessToken, blogId, postsListener)
    }

    private fun getDraftPosts(
        accessToken: String,
        blogId: String, postsListener: ApiManager.PostsListener
    ) {
        postsRepository.requestDraftPosts(accessToken, blogId, postsListener)
    }
}
