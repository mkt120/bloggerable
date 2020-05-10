package com.mkt120.bloggerable.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.mkt120.bloggerable.ApiManager
import com.mkt120.bloggerable.api.OauthResponse
import com.mkt120.bloggerable.datasource.BloggerApiDataSource
import com.mkt120.bloggerable.datasource.PreferenceDataSource
import com.mkt120.bloggerable.model.Account

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

    fun getAccessToken(id: String, now: Long): String? {
        val account = getAccount(id) ?: return null
        return account.getAccessToken(now)
    }

    /**
     * アクセストークンを取得
     */
    fun requestAccessToken(serverAuthCode: String, listener: ApiManager.OauthListener) {
        bloggerApiDataSource.requestAccessToken(serverAuthCode, listener)
    }

    /**
     * リフレッシュトークン
     */
    fun requestRefresh(userId: String, refreshToken: String, listener: OnRefreshListener) {
        bloggerApiDataSource.refreshAccessToken(refreshToken, object : ApiManager.OauthListener {

            override fun onResponse(response: OauthResponse) {
                // アクセストークン
                val accessToken = response.access_token!!
                val expiresIn = System.currentTimeMillis() + response.expires_in!! * 1000L
                preferenceDataSource.saveAccessToken(userId, accessToken, refreshToken, expiresIn)
                listener.onRefresh()
            }

            override fun onErrorResponse(code: Int, message: String) {
                listener.onErrorResponse(code, message)
            }

            override fun onFailed(t: Throwable) {
                listener.onFailed(t)
            }
        })
    }

    fun saveNewAccount(
        account: GoogleSignInAccount,
        accessToken: String,
        expired: Long,
        refreshToken: String
    ) :Account {
        return preferenceDataSource.saveNewAccount(account, accessToken, expired, refreshToken)
    }

    interface OnRefreshListener {
        fun onRefresh()
        fun onErrorResponse(code: Int, message: String)
        fun onFailed(t: Throwable)
    }
}
