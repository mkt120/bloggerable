package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.ApiManager
import com.mkt120.bloggerable.repository.AccessTokenRepository
import com.mkt120.bloggerable.repository.PostsRepository

class CreatePosts(
    private val getAccessToken: GetAccessToken,
    private val postsRepository: PostsRepository
) {
    fun execute(
        blogId: String,
        title: String,
        html: String,
        labels: Array<String>?,
        draft: Boolean,
        completeListener: ApiManager.CompleteListener
    ) {
        val accessToken = getAccessToken.execute(object : AccessTokenRepository.OnRefreshListener {
            override fun onRefresh() {
                execute(blogId, title, html, labels, draft, completeListener)
            }
        })
        if (accessToken != null) {
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