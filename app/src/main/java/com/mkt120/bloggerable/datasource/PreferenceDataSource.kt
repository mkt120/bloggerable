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

    override fun addNewAccount(
        googleAccount: GoogleSignInAccount,
        accessToken: String,
        tokenExpiredDateMillis: Long,
        refreshToken: String
    ): Account {
        val accounts = getAccounts()
        var account = accounts.find { item -> item.getId() == googleAccount.id }
        if (account != null) {
            account.updateAccessToken(accessToken, refreshToken, tokenExpiredDateMillis)
        } else {
            account = Account(googleAccount, accessToken, tokenExpiredDateMillis, refreshToken)
            accounts.add(account)
        }
        prefs.edit().putString(KEY_ACCOUNTS, Gson().toJson(accounts)).apply()
        return account
    }

    override fun updateAccessToken(
        id: String,
        accessToken: String,
        refreshToken: String,
        expired: Long
    ) {
        saveAccount(id, accessToken, refreshToken, expired, 0L)
    }

    override fun updateLastBlogListRequest(account: Account, lastBlogListRequest: Long) {
        saveAccount(account.getId(), null, null, 0L, lastBlogListRequest)
    }

    private fun saveAccount(
        id: String,
        accessToken: String? = null,
        refreshToken: String? = null,
        tokenExpiredDateMillis: Long = 0L,
        lastBlogListRequest: Long = 0L
    ) {
        val accounts = getAccounts()
        val account =
            accounts.find { account -> account.getId() == id } ?: throw IllegalArgumentException()

        if (lastBlogListRequest > 0) {
            account.updateLastBlogListRequest(lastBlogListRequest)
        }
        if (!accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty() && tokenExpiredDateMillis > 0) {
            account.updateAccessToken(accessToken, refreshToken, tokenExpiredDateMillis)
        }
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