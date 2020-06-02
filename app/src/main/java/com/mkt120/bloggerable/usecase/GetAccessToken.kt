package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.repository.Repository
import io.reactivex.Single

class GetAccessToken(
    private val accountRepository: Repository.IAccountRepository,
    private val timeRepository: Repository.ITimeRepository
) :
    UseCase.IGetAccessToken {

    override fun execute(userId: String): Single<String> {
        val current = timeRepository.getCurrentTime()
        return accountRepository.getAccessToken(userId, current)
            .onErrorResumeNext(requestRefresh(userId, current))
    }

    private fun requestRefresh(userId: String, now: Long): Single<String> {
        val refreshToken = accountRepository.getRefreshToken(userId)
        return accountRepository.requestRefresh(userId, refreshToken!!, now)
    }

}