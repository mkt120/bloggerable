package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.model.Account
import com.mkt120.bloggerable.repository.AccountRepository

class GetCurrentAccount(private val preferenceDataSource: AccountRepository) {
    fun execute() : Account? = preferenceDataSource.getCurrentAccount()
}
