package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.model.Account
import com.mkt120.bloggerable.repository.Repository
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test

class SaveCurrentAccountTest {

    companion object {
        private const val STUB_USER_ID = "stubUserId"
        private const val STUB_ACCESS_TOKEN = "stubAccessToken"
        private const val STUB_CURRENT_BLOG_ID = "stubCurrentBlogId"

        private const val STUB_USER_NAME = "stubUserName"
        private const val STUB_USER_URL = "stubUserUrl"
        private const val STUB_TOKEN_EXPIRED = 1000L
        private const val STUB_TOKEN_REFRESH = "stubTokenRefresh"
        private const val STUB_LAST_REQUEST_BLOG_LIST = 1000L
    }

    private val accountRepository = mock<Repository.IAccountRepository>()

    @Test
    fun execute() {
        val account = Account(
            STUB_USER_ID,
            STUB_USER_NAME,
            STUB_USER_URL,
            STUB_ACCESS_TOKEN,
            STUB_TOKEN_EXPIRED,
            STUB_TOKEN_REFRESH,
            STUB_LAST_REQUEST_BLOG_LIST,
            STUB_CURRENT_BLOG_ID
        )
        val save = SaveCurrentAccount(accountRepository)
        save.execute(account)
        verify(accountRepository).setCurrentAccount(account)
    }
}