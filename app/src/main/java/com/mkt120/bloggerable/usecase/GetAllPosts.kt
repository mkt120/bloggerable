package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.ApiManager
import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.model.posts.Posts
import com.mkt120.bloggerable.repository.AccountRepository
import com.mkt120.bloggerable.repository.BlogRepository
import com.mkt120.bloggerable.repository.PostsRepository

class GetAllPosts(
    private val getAccessToken: GetAccessToken,
    private val postsRepository: PostsRepository,
    private val blogRepository: BlogRepository
) {
    fun execute(
        now: Long,
        userId: String,
        isDraft: Boolean,
        blog: Blogs,
        listener: PostsListener
    ) {
        val accessToken =
            getAccessToken.execute(userId, object : AccountRepository.OnRefreshListener {
                override fun onRefresh() {
                    execute(now, userId, isDraft, blog, listener)
                }

                override fun onErrorResponse(code: Int, message: String) {
                    listener.onError(message)
                }

                override fun onFailed(t: Throwable) {
                    listener.onError(t.message!!)
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
        listener: PostsListener
    ) {
        postsRepository.requestLivePosts(accessToken, blog.id!!, object : ApiManager.PostsListener {
            override fun onResponse(posts: List<Posts>?) {
                posts?.let {
                    postsRepository.savePosts(it, false)
                    blogRepository.updateLastPostListRequest(blog, now)
                }
                listener.onComplete()
            }

            override fun onErrorResponse(code: Int, message: String) {
                listener.onError(message)
            }

            override fun onFailed(t: Throwable) {
                listener.onError(t.message!!)
            }
        })
    }

    private fun getDraftPosts(
        now: Long,
        accessToken: String,
        blog: Blogs,
        listener: PostsListener
    ) {
        postsRepository.requestDraftPosts(
            accessToken,
            blog.id!!,
            object : ApiManager.PostsListener {
                override fun onResponse(posts: List<Posts>?) {
                    posts?.let {
                        postsRepository.savePosts(it, true)
                    }
                    blogRepository.updateLastPostListRequest(blog, now)
                    listener.onComplete()
                }

                override fun onErrorResponse(code: Int, message: String) {
                    listener.onError(message)
                }

                override fun onFailed(t: Throwable) {
                    listener.onError(t.message!!)
                }
            })
    }

    interface PostsListener {
        fun onComplete()
        fun onError(message: String)
    }
}
