package com.mkt120.bloggerable.repository

import com.mkt120.bloggerable.api.UserInfoResponse
import com.mkt120.bloggerable.datasource.DataSource
import com.mkt120.bloggerable.model.Account
import io.reactivex.Single

class AccountRepository(
    private val googleOauthApiDataSource: DataSource.IGoogleOauthApiDataSource,
    private val bloggerApiDataSource: DataSource.IBloggerApiDataSource,
    private val preferenceDataSource: DataSource.IPreferenceDataSource
) : Repository.IAccountRepository {
    override fun getAllAccounts(): ArrayList<Account> = preferenceDataSource.getAccounts()

    private fun getAccount(id: String): Account? = preferenceDataSource.getAccount(id)

    override fun setCurrentAccount(account: Account) {
        preferenceDataSource.saveCurrentAccount(account)
    }

    override fun updateLastBlogListRequest(account: Account, now: Long) {
        preferenceDataSource.updateLastBlogListRequest(account, now)
    }

    override fun getCurrentAccount(): Account {
        val account = preferenceDataSource.getCurrentAccount()
        return account ?: preferenceDataSource.getAccounts()[0]
    }

    override fun getRefreshToken(id: String): String? {
        return getAccount(id)?.getRefreshToken()
    }

    override fun getAccessToken(id: String, now: Long): Single<String> {
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
     * アクセストークン・ユーザ情報を取得
     */
    override fun requestUserInfo(accessToken: String): Single<UserInfoResponse> {
        return bloggerApiDataSource.requestUserInfo(accessToken)
    }

    /**
     * アクセストークンをリフレッシュする
     */
    override fun requestRefresh(
        userId: String,
        refreshToken: String,
        now: Long
    ): Single<String> {
        return Single.create<String> { emitter ->
            googleOauthApiDataSource.refreshAccessToken(
                refreshToken,
                { response ->
                    // アクセストークン
                    val accessToken = response.accessToken!!
                    val expiresIn = now + response.accessTokenExpirationTime!! * 1000L
                    preferenceDataSource.updateAccessToken(
                        userId,
                        accessToken,
                        refreshToken,
                        expiresIn
                    )
                    emitter.onSuccess(accessToken)
                },
                {
                    emitter.onError(it)
                })
        }
    }

    override fun saveNewAccount(
        id: String, name: String, photoUrl: String,
        accessToken: String,
        expired: Long,
        refreshToken: String
    ): Account {
        return preferenceDataSource.addNewAccount(
            id,
            name,
            photoUrl,
            accessToken,
            expired,
            refreshToken
        )
    }

}
