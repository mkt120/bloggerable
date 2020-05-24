package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.model.Account
import com.mkt120.bloggerable.repository.Repository

class SaveCurrentAccount(private val accountRepository: Repository.IAccountRepository) {

    fun execute(account: Account) {
        accountRepository.setCurrentAccount(account)
    }
}