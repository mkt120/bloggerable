package com.mkt120.bloggerable.datasource

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.mkt120.bloggerable.ApiManager

class GoogleOauthApiDataSource(val context: Context) {
    companion object {
        const val STRING_SCOPE_BLOGGER = "https://www.googleapis.com/auth/blogger"
    }

    fun getSignInIntent(): Intent {
        val option = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestServerAuthCode(ApiManager.CLIENT_ID, true)
            .requestScopes(Scope(STRING_SCOPE_BLOGGER))
            .build()
        val googleSignInClient = GoogleSignIn.getClient(context, option)
        return googleSignInClient.signInIntent
    }
}