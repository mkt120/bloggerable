package com.mkt120.bloggerable.repository

import com.mkt120.bloggerable.datasource.PreferenceDataSource
import com.mkt120.bloggerable.model.Account

class CurrentBlogIdRepository(private val preferenceDataSource: PreferenceDataSource) {
    fun save(account: Account) {
        preferenceDataSource.saveCurrentAccount(account)
    }

    fun get(): String = preferenceDataSource.getCurrentBlogId()
}