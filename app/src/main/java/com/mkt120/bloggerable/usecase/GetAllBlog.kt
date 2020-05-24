package com.mkt120.bloggerable.usecase

import android.util.Log
import com.mkt120.bloggerable.model.Account
import com.mkt120.bloggerable.repository.BlogRepository
import com.mkt120.bloggerable.repository.Repository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class GetAllBlog(
    private val getAccessToken: GetAccessToken,
    private val accountRepository: Repository.IAccountRepository,
    private val blogsRepository: BlogRepository
) {

    companion object {
        private val TAG = GetAllBlog::class.java.simpleName
    }

    fun execute(
        now: Long,
        account: Account,
        onComplete: () -> Unit,
        onFailed: (Throwable) -> Unit
    ) {
        getAccessToken.execute(account.getId()).flatMap { accessToken ->
            blogsRepository.requestAllBlog(accessToken)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ blogList ->
                Log.d(TAG, "requestAllBlogs onResponse")
                blogList?.let {
                    blogsRepository.saveAllBlog(it)
                    if (it.isNotEmpty()) {
                        accountRepository.updateLastBlogListRequest(account, now)
                    }
                }
                onComplete()
            }, onFailed)
    }
}