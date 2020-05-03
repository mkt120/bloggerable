package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.model.Account
import com.mkt120.bloggerable.repository.AccountRepository

class GetAllAccount(private val accountRepository: AccountRepository) {
    fun execute(): ArrayList<Account> = accountRepository.getAllAccounts()
}
