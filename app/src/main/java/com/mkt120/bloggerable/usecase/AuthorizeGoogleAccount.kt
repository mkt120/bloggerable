package com.mkt120.bloggerable.usecase

import android.content.Intent
import com.mkt120.bloggerable.repository.GoogleAccountRepository

class AuthorizeGoogleAccount(private val googleAccountRepository: GoogleAccountRepository) {
    fun alreadyAuthorized(): Boolean {
        val account =googleAccountRepository.getAccounts()
        return account.isNotEmpty()
    }

    fun getSignInIntent(): Intent = googleAccountRepository.getSignInIntent()

}