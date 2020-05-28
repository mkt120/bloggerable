package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.model.Account
import com.mkt120.bloggerable.repository.Repository
import io.reactivex.Completable

class GetAllBlog(
    private val getAccessToken: UseCase.IGetAccessToken,
    private val accountRepository: Repository.IAccountRepository,
    private val blogsRepository: Repository.IBlogRepository,
    private val timeRepository: Repository.ITimeRepository
) {

    companion object {
        private val TAG = GetAllBlog::class.java.simpleName
    }

    fun execute(
        account: Account
    ): Completable {
        // アクセストークン取得 → リクエスト → 保存
        return getAccessToken.execute(account.getId())
            .flatMap { accessToken ->
                blogsRepository.requestAllBlog(accessToken)
            }
            .flatMapCompletable { blogsList ->
                Completable.create { emitter ->
                    blogsRepository.saveAllBlog(blogsList)
                    if (blogsList.isNotEmpty()) {
                        val now = timeRepository.getCurrentTime()
                        accountRepository.updateLastBlogListRequest(account, now)
                    }
                    emitter.onComplete()
                }
            }
    }
}