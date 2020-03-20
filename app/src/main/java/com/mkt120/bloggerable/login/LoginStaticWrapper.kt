package com.mkt120.bloggerable.login

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.mkt120.bloggerable.ApiManager
import com.mkt120.bloggerable.util.PreferenceManager

class LoginStaticWrapper(val context: Context) {
    companion object {
        const val STRING_SCOPE_BLOGGER = "https://www.googleapis.com/auth/blogger"
    }
    fun getClient(): Intent {
        val option = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestServerAuthCode(ApiManager.CLIENT_ID, true)
            .requestScopes(Scope(STRING_SCOPE_BLOGGER))
            .build()
        val googleSignInClient = GoogleSignIn.getClient(context, option)
        return googleSignInClient.signInIntent
    }

    fun getSignedInAccountFromIntent(data:Intent?): Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
    fun isExpiredDateMillis(): Boolean = PreferenceManager.isExpiredDateMillis()
    fun refreshToken(): String = PreferenceManager.refreshToken
}
