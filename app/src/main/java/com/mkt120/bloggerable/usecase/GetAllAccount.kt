package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.model.Account
import com.mkt120.bloggerable.repository.Repository

class GetAllAccount(private val accountRepository: Repository.IAccountRepository) {
    fun execute(): ArrayList<Account> = accountRepository.getAllAccounts()
}
