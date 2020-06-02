package com.mkt120.bloggerable.datasource

import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mkt120.bloggerable.model.Account

class PreferenceDataSource(context: Context) : DataSource.IPreferenceDataSource {
    companion object {
        private const val KEY_ACCOUNTS = "KEY_ACCOUNTS"
        private const val KEY_CURRENT_ACCOUNT_ID = "KEY_CURRENT_ACCOUNT_ID"
    }

    private var prefs: SharedPreferences =
        context.getSharedPreferences(context.packageName + "_preferences", Context.MODE_PRIVATE);

    override fun getCurrentAccount(): Account? {
        val id = prefs.getString(KEY_CURRENT_ACCOUNT_ID, "")!!
        return getAccount(id) ?: getAccounts()[0]
    }

    override fun saveCurrentAccount(account: Account) {
        prefs.edit().putString(KEY_CURRENT_ACCOUNT_ID, account.getId()).apply()
    }

    override fun saveNewAccount(
        account: GoogleSignInAccount,
        accessToken: String,
        tokenExpiredDateMillis: Long,
        refreshToken: String
    ): Account {
        return saveNewAccount(
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
            saveAccount(it, accessToken, expired, refreshToken)
        }
    }

    override fun saveAccount(account: Account, lastBlogListRequest: Long) {
        val accounts = getAccounts()
        val found = accounts.find { item -> item.getId() == account.getId() }
        found?.updateLastBlogListRequest(lastBlogListRequest)
        prefs.edit().putString(KEY_ACCOUNTS, Gson().toJson(accounts)).apply()
    }

    private fun saveAccount(
        account: Account,
        accessToken: String,
        tokenExpiredDateMillis: Long,
        refreshToken: String
    ) {
        val accounts = getAccounts()
        val found = accounts.find { item -> item.getId() == account.getId() }
        found?.updateAccessToken(accessToken, refreshToken, tokenExpiredDateMillis)
        prefs.edit().putString(KEY_ACCOUNTS, Gson().toJson(accounts)).apply()
    }

    override fun getAccounts(): ArrayList<Account> {
        val json = prefs.getString(KEY_ACCOUNTS, null) ?: return arrayListOf()
        val typeToken = object : TypeToken<ArrayList<Account>>() {}
        return Gson().fromJson(json, typeToken.type)
    }

    override fun getAccount(id: String): Account? {
        val accounts = getAccounts()
        return accounts.find { account -> account.getId() == id }
    }
}