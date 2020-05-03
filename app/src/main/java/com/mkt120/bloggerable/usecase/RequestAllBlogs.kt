package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.ApiManager
import com.mkt120.bloggerable.model.Account
import com.mkt120.bloggerable.repository.AccountRepository
import com.mkt120.bloggerable.repository.BlogRepository

class RequestAllBlogs(
    private val getAccessToken: GetAccessToken,
    private val blogsRepository: BlogRepository
) {

    fun execute(account: Account, listener: ApiManager.BlogListener) {
        val accessToken =
            getAccessToken.execute(account.getId(), object : AccountRepository.OnRefreshListener {
                override fun onRefresh() {
                    execute(account, listener)
                }
                override fun onErrorResponse(code: Int, message: String) {
                    listener.onErrorResponse(code, message)
                }
                override fun onFailed(t: Throwable) {
                    listener.onFailed(t)
                }
            })

        accessToken?.let {
            requestAllBlogs(accessToken, listener)
        }
    }

    private fun requestAllBlogs(accessToken: String, listener: ApiManager.BlogListener) {
        blogsRepository.requestAllBlog(accessToken, listener)
    }
}