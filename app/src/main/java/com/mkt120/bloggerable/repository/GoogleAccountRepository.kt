package com.mkt120.bloggerable.repository

import android.content.Intent
import com.mkt120.bloggerable.datasource.DataSource
import com.mkt120.bloggerable.model.Account

class GoogleAccountRepository(
    private val preferenceDataSource: DataSource.IPreferenceDataSource,
    private val googleOauthApiDataSource: DataSource.IGoogleOauthApiDataSource
) : Repository.IGoogleAccountRepository {

    override fun getSignInIntent(): Intent {
        return googleOauthApiDataSource.getSignInIntent()
    }

    override fun getAccounts(): ArrayList<Account> {
        //todo:
        return preferenceDataSource.getAccounts()
    }
}