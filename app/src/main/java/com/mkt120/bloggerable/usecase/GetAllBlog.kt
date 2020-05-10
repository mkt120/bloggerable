package com.mkt120.bloggerable.usecase

import android.os.Handler
import com.mkt120.bloggerable.ApiManager
import com.mkt120.bloggerable.model.Account
import com.mkt120.bloggerable.repository.AccountRepository
import com.mkt120.bloggerable.repository.BlogRepository

class GetAllBlog(
    private val getAccessToken: GetAccessToken,
    private val accountRepository: AccountRepository,
    private val blogsRepository: BlogRepository
) {

    fun execute(now: Long, account: Account, listener: ApiManager.BlogListener) {
        if (!account.isExpiredBlogList(now)) {
            val blogs = blogsRepository.findAllBlog(account.getId())
            Handler().postDelayed(Runnable {
                listener.onResponse(blogs)
            }, 500)
            return
        }
        val accessToken =
            getAccessToken.execute(account.getId(), object : AccountRepository.OnRefreshListener {
                override fun onRefresh() {
                    execute(now, account, listener)
                }

                override fun onErrorResponse(code: Int, message: String) {
                    listener.onErrorResponse(code, message)
                }

                override fun onFailed(t: Throwable) {
                    listener.onFailed(t)
                }
            })

        accessToken?.let {
            requestAllBlogs(now, account, accessToken, listener)
        }
    }

    private fun requestAllBlogs(
        now: Long,
        account: Account,
        accessToken: String,
        listener: ApiManager.BlogListener
    ) {
        blogsRepository.requestAllBlog(
            accessToken,
            { blogList ->
                blogList?.let {
                    blogsRepository.saveAllBlog(it)
                    accountRepository.updateLastBlogListRequest(account, now)
                }
                listener.onResponse(blogList)
            },
            { code, message ->
                listener.onErrorResponse(code, message)
            },
            { t ->
                listener.onFailed(t)
            }
        )
    }
}