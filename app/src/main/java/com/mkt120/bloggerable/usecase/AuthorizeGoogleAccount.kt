package com.mkt120.bloggerable.usecase

import android.content.Intent
import com.mkt120.bloggerable.repository.Repository

class AuthorizeGoogleAccount(private val googleAccountRepository: Repository.IGoogleAccountRepository) {
    fun alreadyAuthorized(): Boolean {
        val account = googleAccountRepository.getAccounts()
        return account.isNotEmpty()
    }

    fun getSignInIntent(): Intent = googleAccountRepository.getSignInIntent()

}