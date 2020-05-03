package com.mkt120.bloggerable.repository

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.mkt120.bloggerable.datasource.GoogleOauthApiDataSource
import com.mkt120.bloggerable.datasource.PreferenceDataSource
import com.mkt120.bloggerable.model.Account

class GoogleAccountRepository(
    private val preferenceDataSource: PreferenceDataSource,
    private val googleOauthApiDataSource: GoogleOauthApiDataSource
) {

    fun getSignInIntent(): Intent {
        return googleOauthApiDataSource.getSignInIntent()
    }

    fun getAccounts():ArrayList<Account> {
        //todo:
        return preferenceDataSource.getAccounts()
    }
}