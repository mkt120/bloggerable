package com.mkt120.bloggerable.repository

import com.mkt120.bloggerable.ApiManager
import com.mkt120.bloggerable.api.OauthResponse
import com.mkt120.bloggerable.datasource.BloggerApiDataSource
import com.mkt120.bloggerable.datasource.PreferenceDataSource

class AccessTokenRepository(
    private val bloggerApiDataSource: BloggerApiDataSource,
    private val preferenceDataSource: PreferenceDataSource
) {

    fun getAccessToken(): String = preferenceDataSource.getAccessToken()

    fun isExpiredAccessToken(): Boolean {
        val expired = preferenceDataSource.getExpiredDateMillis()
        return System.currentTimeMillis() >= expired
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
    fun requestRefresh(listener: OnRefreshListener) {
        val refreshToken = preferenceDataSource.getRefreshToken()

        bloggerApiDataSource.refreshAccessToken(refreshToken, object : ApiManager.OauthListener {
            override fun onResponse(response: OauthResponse) {
                // アクセストークン
                response.access_token?.let {
                    preferenceDataSource.saveAccessToken(it)
                }
                // リフレッシュトークン
                response.refresh_token?.let {
                    preferenceDataSource.saveRefreshToken(it)
                }
                // 有効期限
                response.expires_in?.let {
                    val expiresIn = System.currentTimeMillis() + it * 1000L
                    preferenceDataSource.saveExpiredDateMillis(expiresIn)
                }
                listener.onRefresh()
            }

            override fun onErrorResponse(code: Int, message: String) {
                //todo:
            }

            override fun onFailed(t: Throwable?) {
                //todo:
            }
        })
    }

    fun saveAccessToken(accessToken:String) {
        preferenceDataSource.saveAccessToken(accessToken)
    }
    fun saveRefreshToken(refreshToken:String) {
        preferenceDataSource.saveRefreshToken(refreshToken)
    }
    fun saveExpiredDateMillis(expires:Long) {
        preferenceDataSource.saveExpiredDateMillis(expires)
    }

    interface OnRefreshListener {
        fun onRefresh()
    }
}
