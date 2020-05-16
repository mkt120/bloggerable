package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.repository.AccessTokenRepository

class GetAccessToken(private val accessTokenRepository: AccessTokenRepository) {

    fun execute(listener: AccessTokenRepository.OnRefreshListener): String? {
        val isExpired = accessTokenRepository.isExpiredAccessToken()
        if (isExpired) {
            accessTokenRepository.requestRefresh(listener)
        }
        return accessTokenRepository.getAccessToken()
    }

}