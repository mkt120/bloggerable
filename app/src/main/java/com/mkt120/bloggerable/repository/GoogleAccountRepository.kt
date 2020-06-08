package com.mkt120.bloggerable.repository

import android.content.Intent
import com.mkt120.bloggerable.datasource.DataSource
import com.mkt120.bloggerable.model.Account
import net.openid.appauth.AuthorizationResponse

class GoogleAccountRepository(
    private val preferenceDataSource: DataSource.IPreferenceDataSource,
    private val googleOauthApiDataSource: DataSource.IGoogleOauthApiDataSource
) : Repository.IGoogleAccountRepository {

    override fun getAuthorizeIntent() :Intent = googleOauthApiDataSource.getAuthorizeIntent()

    override fun requestAccessToken(
        response: AuthorizationResponse,
        onResponse: (accessToken: String, refreshToken: String, tokenExpired: Long) -> Unit,
        onFailed: (Throwable) -> Unit
    ) {
        googleOauthApiDataSource.requestAccessToken(response, onResponse, onFailed)
    }

    override fun getAccounts(): ArrayList<Account> {
        //todo:
        return preferenceDataSource.getAccounts()
    }
}