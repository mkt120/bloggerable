package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.model.Account
import com.mkt120.bloggerable.repository.CurrentBlogIdRepository

class SaveCurrentBlogId(private val lastSelectBlogId: CurrentBlogIdRepository) {

    fun execute(account: Account) {
        lastSelectBlogId.save(account)
    }
}