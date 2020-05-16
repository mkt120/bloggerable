package com.mkt120.bloggerable.repository

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.mkt120.bloggerable.datasource.GoogleOauthApiDataSource
import com.mkt120.bloggerable.datasource.PreferenceDataSource

class GoogleAccountRepository(
    private val preferenceDataSource: PreferenceDataSource,
    private val googleOauthApiDataSource: GoogleOauthApiDataSource
) {
    fun getAccessToken() :String {
        return preferenceDataSource.getAccessToken()
    }
    fun save(account: GoogleSignInAccount) {
        preferenceDataSource.savePhotoUrl(account.photoUrl.toString())
        preferenceDataSource.saveDisplayName(account.displayName.toString())
    }

    fun getSignInIntent(): Intent {
        return googleOauthApiDataSource.getSignInIntent()
    }

    fun getPhotoUrl() = preferenceDataSource.getPhotoUrl()
    fun getDisplayName() = preferenceDataSource.getDisplayName()
}