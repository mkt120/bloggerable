package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.ApiManager
import com.mkt120.bloggerable.repository.AccessTokenRepository
import com.mkt120.bloggerable.repository.BlogsRepository

class RequestAllBlogs(
    private val getAccessToken: GetAccessToken,
    private val blogsRepository: BlogsRepository
) {

    fun execute(listener: ApiManager.BlogListener) {
        val accessToken = getAccessToken.execute(object : AccessTokenRepository.OnRefreshListener {
            override fun onRefresh() {
                execute(listener)
            }
        })
        if (accessToken != null) {
            requestAllBlogs(accessToken, listener)
        }
    }

    private fun requestAllBlogs(accessToken: String, listener: ApiManager.BlogListener) {
        blogsRepository.requestAllBlogs(accessToken, listener)
    }
}