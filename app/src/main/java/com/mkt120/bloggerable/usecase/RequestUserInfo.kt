package com.mkt120.bloggerable.usecase

import android.content.Intent
import com.mkt120.bloggerable.model.Account
import com.mkt120.bloggerable.repository.Repository
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse

class RequestUserInfo(
    private val timeRepository: Repository.ITimeRepository,
    private val accountRepository: Repository.IAccountRepository,
    private val googleAccountRepository: Repository.IGoogleAccountRepository
) {

    fun execute(
        intent: Intent,
        onComplete: (account: Account) -> Unit,
        onFailed: (Throwable) -> Unit
    ) {
        val error = AuthorizationException.fromIntent(intent)
        if (error != null) {
            onFailed(error)
            return
        }
        val response = AuthorizationResponse.fromIntent(intent) ?: return
        googleAccountRepository.requestAccessToken(
            response,
            { accessToken, refreshToken, tokenExpired ->
                getUserInfo(accessToken, refreshToken, tokenExpired, onComplete, onFailed)
            }, onFailed
        )
    }

    private fun getUserInfo(
        accessToken: String,
        refreshToken: String,
        tokenExpired: Long,
        onComplete: (account: Account) -> Unit,
        onFailed: (Throwable) -> Unit
    ) {
        accountRepository.requestUserInfo(accessToken).subscribe({ response ->
            // アクセストークン
            // リフレッシュトークン
            // 有効期限
            val expiresIn = timeRepository.getCurrentTime() + (tokenExpired * 1000L)
            val newAccount = accountRepository.saveNewAccount(
                response.id,
                response.name,
                response.picture,
                accessToken,
                expiresIn,
                refreshToken
            )
            onComplete(newAccount)
        }, onFailed)
    }
}
