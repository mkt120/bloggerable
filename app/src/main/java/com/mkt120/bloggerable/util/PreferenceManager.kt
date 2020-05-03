package com.mkt120.bloggerable.util

import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mkt120.bloggerable.model.Account

object PreferenceManager {

    private lateinit var prefs: SharedPreferences

    private const val KEY_ACCOUNTS = "KEY_ACCOUNTS"
    private const val KEY_CURRENT_ACCOUNT_ID = "KEY_CURRENT_ACCOUNT_ID"
    private const val KEY_LAST_SELECT_BLOG_ID = "KEY_LAST_SELECT_BLOG_ID"


    fun init(context: Context) {
        prefs =
            context.getSharedPreferences(context.packageName + "_preferences", Context.MODE_PRIVATE)
    }

    var currentBlogId: String
        set(value) = prefs.edit().putString(KEY_LAST_SELECT_BLOG_ID, value).apply()
        get() = prefs.getString(KEY_LAST_SELECT_BLOG_ID, "")!!

    fun setCurrentAccount(account: Account) {
        prefs.edit().putString(KEY_CURRENT_ACCOUNT_ID, account.getId()).apply()
    }

    fun getCurrentAccount(): Account {
        val id = prefs.getString(KEY_CURRENT_ACCOUNT_ID, "")!!
        return getAccount(id) ?: getAccounts()[0]
    }

    fun getAccount(id: String): Account? {
        val accounts = getAccounts()
        return accounts.find { account -> account.getId() == id }
    }

    fun getAccounts(): ArrayList<Account> {
        val json = prefs.getString(KEY_ACCOUNTS, null) ?: return arrayListOf()
        val typeToken = object : TypeToken<ArrayList<Account>>() {}
        return Gson().fromJson<ArrayList<Account>>(json, typeToken.type)
    }

    fun saveAccount(
        newAccount: Account,
        accessToken: String,
        tokenExpiredDateMillis: Long,
        refreshToken: String
    ) {
        val accounts = getAccounts()
        val found = accounts.find { item -> item.getId() == newAccount.getId() }
        found?.updateAccessToken(accessToken, refreshToken, tokenExpiredDateMillis)
        prefs.edit().putString(KEY_ACCOUNTS, Gson().toJson(accounts)).apply()
    }

    fun saveNewAccount(
        newAccount: GoogleSignInAccount,
        accessToken: String,
        tokenExpiredDateMillis: Long,
        refreshToken: String
    ) :Account {
        val accounts = getAccounts()
        var account = accounts.find { item -> item.getId() == newAccount.id }
        if (account != null) {
            account.updateAccessToken(accessToken, refreshToken, tokenExpiredDateMillis)
        } else {
            account = Account(newAccount, accessToken, tokenExpiredDateMillis, refreshToken)
            accounts.add(account)
        }
        prefs.edit().putString(KEY_ACCOUNTS, Gson().toJson(accounts)).apply()
        return account
    }
}