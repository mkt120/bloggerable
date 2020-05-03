package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.repository.AccountRepository

class GetAccessToken(private val accountRepository: AccountRepository) {


    fun execute(userId: String, listener: AccountRepository.OnRefreshListener): String? {
        val token =
            accountRepository.getAccessToken(userId, System.currentTimeMillis()) ?: return null
        if (token.isEmpty()) {
            val refreshToken = accountRepository.getRefreshToken(userId)
            accountRepository.requestRefresh(userId, refreshToken!!, listener)
            return null
        }
        return token
    }

}