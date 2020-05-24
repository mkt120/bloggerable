package com.mkt120.bloggerable.datasource

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.mkt120.bloggerable.model.Account
import com.mkt120.bloggerable.util.PreferenceManager

class PreferenceDataSource : DataSource.IPreferenceDataSource {

    override fun getCurrentAccount(): Account? = PreferenceManager.getCurrentAccount()

    override fun saveCurrentAccount(account: Account) {
        PreferenceManager.setCurrentAccount(account)
    }

    override fun saveAccount(account: Account, lastBlogListRequest: Long) {
        PreferenceManager.saveAccount(account, lastBlogListRequest)
    }

    override fun saveNewAccount(
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

    override fun saveAccessToken(
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

    override fun getAccounts(): ArrayList<Account> = PreferenceManager.getAccounts()

    override fun getAccount(id: String): Account? = PreferenceManager.getAccount(id)
}