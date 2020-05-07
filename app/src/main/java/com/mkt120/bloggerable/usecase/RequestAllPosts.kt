package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.ApiManager
import com.mkt120.bloggerable.api.PostResponse
import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.repository.AccountRepository
import com.mkt120.bloggerable.repository.PostsRepository

class RequestAllPosts(
    private val getAccessToken: GetAccessToken,
    private val postsRepository: PostsRepository
) {
    fun execute(
        now: Long,
        userId: String,
        isDraft: Boolean,
        blog: Blogs,
        listener: ApiManager.PostsListener
    ) {
        val accessToken =
            getAccessToken.execute(userId, object : AccountRepository.OnRefreshListener {
                override fun onRefresh() {
                    execute(now, userId, isDraft, blog, listener)
                }

                override fun onErrorResponse(code: Int, message: String) {
                    listener.onErrorResponse(code, message)
                }

                override fun onFailed(t: Throwable) {
                    listener.onFailed(t)
                }
            })
        accessToken?.let {
            if (isDraft) {
                getDraftPosts(now, accessToken, blog, listener)
            } else {
                getLivePosts(now, accessToken, blog, listener)
            }
        }
    }

    private fun getLivePosts(
        now: Long,
        accessToken: String,
        blog: Blogs,
        listener: ApiManager.PostsListener
    ) {
        postsRepository.requestLivePosts(accessToken, blog.id!!, object : ApiManager.PostsListener {
            override fun onResponse(post: PostResponse?) {
                blog.updateLastRequest(now)
                listener.onResponse(post)
            }

            override fun onErrorResponse(code: Int, message: String) {
                listener.onErrorResponse(code, message)
            }

            override fun onFailed(t: Throwable) {
                listener.onFailed(t)
            }
        })
    }

    private fun getDraftPosts(
        now: Long,
        accessToken: String,
        blog: Blogs,
        listener: ApiManager.PostsListener
    ) {
        postsRepository.requestDraftPosts(
            accessToken,
            blog.id!!,
            object : ApiManager.PostsListener {
                override fun onResponse(post: PostResponse?) {
                    blog.updateLastRequest(now)
                    listener.onResponse(post)
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
