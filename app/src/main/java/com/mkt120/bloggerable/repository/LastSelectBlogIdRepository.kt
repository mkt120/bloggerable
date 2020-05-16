package com.mkt120.bloggerable.repository

import com.mkt120.bloggerable.datasource.PreferenceDataSource

class LastSelectBlogIdRepository(private val preferenceDataSource: PreferenceDataSource) {
    fun save(blogId: String) {
        preferenceDataSource.saveLastSelectBlogId(blogId)
    }

    fun get(): String = preferenceDataSource.getLastSelectBlogId()
}