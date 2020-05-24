package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.repository.Repository
import io.reactivex.Single

class GetAccessToken(private val accountRepository: Repository.IAccountRepository) {

    fun execute(userId: String): Single<String> {
        return accountRepository.getAccessToken(userId, System.currentTimeMillis())
            .onErrorResumeNext(requestRefresh(userId))
    }

    private fun requestRefresh(userId: String): Single<String> {
        val refreshToken = accountRepository.getRefreshToken(userId)
        return accountRepository.requestRefresh(userId, refreshToken!!)
    }

}