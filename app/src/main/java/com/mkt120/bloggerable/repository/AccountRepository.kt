package com.mkt120.bloggerable.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.mkt120.bloggerable.api.OauthResponse
import com.mkt120.bloggerable.datasource.BloggerApiDataSource
import com.mkt120.bloggerable.datasource.PreferenceDataSource
import com.mkt120.bloggerable.model.Account
import io.reactivex.Single

class AccountRepository(
    private val bloggerApiDataSource: BloggerApiDataSource,
    private val preferenceDataSource: PreferenceDataSource
) {
    fun getAllAccounts(): ArrayList<Account> = preferenceDataSource.getAccounts()

    private fun getAccount(id: String): Account? = preferenceDataSource.getAccount(id)

    fun setCurrentAccount(account: Account) {
        preferenceDataSource.saveCurrentAccount(account)
    }

    fun updateLastBlogListRequest(account: Account, now: Long) {
        preferenceDataSource.saveAccount(account, now)
    }

    fun getCurrentAccount(): Account {
        val account = preferenceDataSource.getCurrentAccount()
        return account ?: preferenceDataSource.getAccounts()[0]
    }

    fun getRefreshToken(id: String): String? {
        return getAccount(id)?.getRefreshToken()
    }

    fun getAccessToken(id: String, now: Long): Single<String> {
        return Single.create { emitter ->
            val account = getAccount(id)
            val token = account?.getAccessToken(now)
            if (token == null || token.isEmpty()) {
                emitter.onError(Exception("access token is expired."))
            } else {
                emitter.onSuccess(token)
            }
        }
    }

    /**
     * アクセストークンを取得
     */
    fun requestAccessToken(serverAuthCode: String): Single<OauthResponse> {
        return bloggerApiDataSource.requestAccessToken(serverAuthCode)
    }

    /**
     * リフレッシュトークン
     */
    fun requestRefresh(userId: String, refreshToken: String): Single<String> {
        return Single.create { emitter ->
            bloggerApiDataSource.refreshAccessToken(refreshToken).subscribe({ response ->
                // アクセストークン
                val accessToken = response.access_token!!
                val expiresIn = System.currentTimeMillis() + response.expires_in!! * 1000L
                preferenceDataSource.saveAccessToken(userId, accessToken, refreshToken, expiresIn)
                emitter.onSuccess(accessToken)
            }, { t -> emitter.onError(t) })
        }
    }

    fun saveNewAccount(
        account: GoogleSignInAccount,
        accessToken: String,
        expired: Long,
        refreshToken: String
    ): Account {
        return preferenceDataSource.saveNewAccount(account, accessToken, expired, refreshToken)
    }

}
