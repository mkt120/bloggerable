package com.mkt120.bloggerable.datasource

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.mkt120.bloggerable.model.Account
import com.mkt120.bloggerable.util.PreferenceManager

class PreferenceDataSource {

    fun getCurrentBlogId(): String = PreferenceManager.currentBlogId

    fun getCurrentAccount(): Account? = PreferenceManager.getCurrentAccount()

    fun saveCurrentAccount(account: Account) {
        PreferenceManager.setCurrentAccount(account)
    }

    fun saveAccount(account: Account, lastBlogListRequest: Long) {
        PreferenceManager.saveAccount(account, lastBlogListRequest)
    }

    fun saveNewAccount(
        account: GoogleSignInAccount,
        accessToken: String,
        tokenExpiredDateMillis: Long,
        refreshToken: String
    ): Account {
        return PreferenceManager.saveNewAccount(
            account,
            accessToken,
            tokenExpiredDateMillis,
            refreshToken
        )
    }

    fun saveAccessToken(
        id: String,
        accessToken: String,
        refreshToken: String,
        expired: Long
    ) {
        val account = getAccount(id)
        account?.let {
            it.updateAccessToken(accessToken, refreshToken, expired)
            PreferenceManager.saveAccount(it, accessToken, expired, refreshToken)

        }
    }

    fun getAccounts(): ArrayList<Account> = PreferenceManager.getAccounts()

    fun getAccount(id: String): Account? = PreferenceManager.getAccount(id)
}