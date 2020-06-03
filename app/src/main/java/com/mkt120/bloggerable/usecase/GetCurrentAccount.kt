package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.model.Account
import com.mkt120.bloggerable.repository.Repository

class GetCurrentAccount(private val accountRepository: Repository.IAccountRepository) :UseCase.IGetCurrentAccount {
    override fun execute(): Account? = accountRepository.getCurrentAccount()
}
