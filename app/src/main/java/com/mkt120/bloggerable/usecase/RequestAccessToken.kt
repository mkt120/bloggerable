package com.mkt120.bloggerable.usecase

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.mkt120.bloggerable.ApiManager
import com.mkt120.bloggerable.api.OauthResponse
import com.mkt120.bloggerable.repository.AccountRepository

class RequestAccessToken(private val accountRepository: AccountRepository) {

    fun execute(intent: Intent?, listener: OnCompleteListener) {
        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(intent)
        val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
        val serverAuthCode = account!!.serverAuthCode!!

        accountRepository.requestAccessToken(serverAuthCode, object : ApiManager.OauthListener {
            override fun onResponse(response: OauthResponse) {
                response.let {
                    // アクセストークン
                    // リフレッシュトークン
                    // 有効期限
                    val expiresIn = System.currentTimeMillis() + (it.expires_in!! * 1000L)
                    val newAccount = accountRepository.saveNewAccount(account, it.access_token!!, expiresIn, it.refresh_token!!)
                    accountRepository.setCurrentAccount(newAccount)
                }
                listener.onComplete()
            }

            override fun onErrorResponse(code: Int, message: String) {
                listener.onErrorResponse(code, message)
            }

            override fun onFailed(t: Throwable) {
                listener.onFailed(t)
            }
        })
    }

    interface OnCompleteListener {
        fun onComplete()
        fun onErrorResponse(code: Int, message: String)
        fun onFailed(t: Throwable)
    }
}
