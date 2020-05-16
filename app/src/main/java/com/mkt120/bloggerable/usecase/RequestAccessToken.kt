package com.mkt120.bloggerable.usecase

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.mkt120.bloggerable.ApiManager
import com.mkt120.bloggerable.api.OauthResponse
import com.mkt120.bloggerable.repository.AccessTokenRepository

class RequestAccessToken(private val accessTokenRepository: AccessTokenRepository) {

    fun execute(intent: Intent?, listener: OnCompleteListener) {

        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(intent)
        val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
        val serverAuthCode= account!!.serverAuthCode!!

        accessTokenRepository.requestAccessToken(serverAuthCode, object : ApiManager.OauthListener {
            override fun onResponse(response: OauthResponse) {
                // アクセストークン
                response.access_token?.let {
                    accessTokenRepository.saveAccessToken(it)
                }
                // リフレッシュトークン
                response.refresh_token?.let {
                    accessTokenRepository.saveRefreshToken(it)
                }
                // 有効期限
                response.expires_in?.let {
                    val expiresIn = System.currentTimeMillis() + it * 1000L
                    accessTokenRepository.saveExpiredDateMillis(expiresIn)
                }
                listener.onComplete()
            }

            override fun onErrorResponse(code: Int, message: String) {
                listener.onErrorResponse(code, message)
            }

            override fun onFailed(t: Throwable?) {
                listener.onFailed(t)
            }
        })
    }

    interface OnCompleteListener {
        fun onComplete()
        fun onErrorResponse(code:Int, message:String)
        fun onFailed(t:Throwable?)
    }
}
