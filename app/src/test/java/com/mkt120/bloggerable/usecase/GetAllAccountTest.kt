package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.model.Account
import com.mkt120.bloggerable.repository.Repository
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GetAllAccountTest {
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

    private val accountList = ArrayList<Account>().apply {
        add(
            Account(
                STUB_USER_ID,
                STUB_USER_NAME,
                STUB_USER_URL,
                STUB_ACCESS_TOKEN,
                STUB_TOKEN_EXPIRED,
                STUB_TOKEN_REFRESH,
                STUB_LAST_REQUEST_BLOG_LIST,
                STUB_CURRENT_BLOG_ID
            )
        )
    }
    private val accountRepository = mock<Repository.IAccountRepository>() {
        on {
            getAllAccounts()
        } doReturn (accountList)
    }

    @Before
    fun setUp() {
    }

    @Test
    fun execute() {
        val get = GetAllAccount(accountRepository)
        val ret = get.execute()
        Assert.assertEquals(ret, accountList)
    }
}