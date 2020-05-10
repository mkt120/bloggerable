package com.mkt120.bloggerable.usecase

import android.util.Log
import com.mkt120.bloggerable.model.Account
import com.mkt120.bloggerable.repository.AccountRepository
import com.mkt120.bloggerable.repository.BlogRepository

class GetAllBlog(
    private val getAccessToken: GetAccessToken,
    private val accountRepository: AccountRepository,
    private val blogsRepository: BlogRepository
) {

    companion object {
        private val TAG = GetAllBlog::class.java.simpleName
    }

    fun execute(now: Long, account: Account, listener: OnCompleteListener) {
        val accessToken =
            getAccessToken.execute(account.getId(), object : AccountRepository.OnRefreshListener {
                override fun onRefresh() {
                    execute(now, account, listener)
                }

                override fun onErrorResponse(code: Int, message: String) {
                    listener.onFailed()
                }

                override fun onFailed(t: Throwable) {
                    listener.onFailed()
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
        listener: OnCompleteListener
    ) {
        blogsRepository.requestAllBlog(
            accessToken,
            { blogList ->
                Log.d(TAG, "requestAllBlogs onResponse")
                blogList?.let {
                    blogsRepository.saveAllBlog(it)
                    if (it.isNotEmpty()) {
                        accountRepository.updateLastBlogListRequest(account, now)
                    }
                }
                listener.onComplete()
            },
            { code, message ->
                Log.d(TAG, "requestAllBlogs onError code=$code, message=$message")
                listener.onFailed()
            },
            { t ->
                Log.d(TAG, "requestAllBlogs onFailed", t)
                listener.onFailed()
            }
        )
    }

    interface OnCompleteListener {
        fun onComplete()
        fun onFailed()
    }
}