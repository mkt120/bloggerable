package com.mkt120.bloggerable.usecase

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.mkt120.bloggerable.repository.GoogleAccountRepository

class AuthorizeGoogleAccount(private val googleAccountRepository: GoogleAccountRepository) {
    fun getSignInIntent() : Intent = googleAccountRepository.getSignInIntent()
    fun getAccessToken() : String = googleAccountRepository.getAccessToken()

    fun saveAccountInfo(intent: Intent?) {
        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(intent)
        val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
        if (account != null) {
            googleAccountRepository.save(account)
        }
    }
}