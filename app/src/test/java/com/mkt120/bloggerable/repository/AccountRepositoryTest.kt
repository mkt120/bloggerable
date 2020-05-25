package com.mkt120.bloggerable.repository

import com.mkt120.bloggerable.api.OauthResponse
import com.mkt120.bloggerable.datasource.DataSource
import com.mkt120.bloggerable.model.Account
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Single
import org.junit.Assert
import org.junit.Test

class AccountRepositoryTest {
    companion object {
        private const val STUB_USER_ID = "stubUserId"
        private const val STUB_USER_ID_1 = "stubUserId1"
        private const val STUB_USER_ID_2 = "stubUserId2"
        private const val STUB_USER_ID_3 = "stubUserId3"
        private const val STUB_USER_ID_4 = "stubUserId4"
        private const val STUB_ACCESS_TOKEN = "stubAccessToken"
        private val STUB_NOW = System.currentTimeMillis()

        private const val STUB_USER_NAME = "stubUserName"
        private const val STUB_USER_URL = "stubUserUrl"
        private const val STUB_TOKEN_EXPIRED = 1000L
        private const val STUB_TOKEN_REFRESH_1 = "stubTokenRefresh1"
        private const val STUB_TOKEN_REFRESH_2 = "stubTokenRefresh2"
        private const val STUB_TOKEN_REFRESH_3 = "stubTokenRefresh3"
        private const val STUB_TOKEN_REFRESH_4 = "stubTokenRefresh4"
        private const val STUB_LAST_REQUEST_BLOG_LIST = 1000L
        private const val STUB_CURRENT_BLOG_ID = "stubCurrentBlogId"

        private const val STUB_SERVER_AUTH_CODE = "stubServerAuthCode"
    }

    private val list = ArrayList<Account>().apply {
        add(
            Account(
                STUB_USER_ID_1,
                STUB_USER_NAME,
                STUB_USER_URL,
                STUB_ACCESS_TOKEN,
                0,
                STUB_TOKEN_REFRESH_1,
                STUB_LAST_REQUEST_BLOG_LIST,
                STUB_CURRENT_BLOG_ID
            )
        )
        add(
            Account(
                STUB_USER_ID_2,
                STUB_USER_NAME,
                STUB_USER_URL,
                STUB_ACCESS_TOKEN,
                0,
                STUB_TOKEN_REFRESH_2,
                STUB_LAST_REQUEST_BLOG_LIST,
                STUB_CURRENT_BLOG_ID
            )
        )
        add(
            Account(
                STUB_USER_ID_3,
                STUB_USER_NAME,
                STUB_USER_URL,
                STUB_ACCESS_TOKEN,
                0,
                STUB_TOKEN_REFRESH_3,
                STUB_LAST_REQUEST_BLOG_LIST,
                STUB_CURRENT_BLOG_ID
            )
        )
        add(
            Account(
                STUB_USER_ID_4,
                STUB_USER_NAME,
                STUB_USER_URL,
                STUB_ACCESS_TOKEN,
                0,
                STUB_TOKEN_REFRESH_4,
                STUB_LAST_REQUEST_BLOG_LIST,
                STUB_CURRENT_BLOG_ID
            )
        )
    }

    private val stub = Account(
        STUB_USER_ID,
        STUB_USER_NAME,
        STUB_USER_URL,
        STUB_ACCESS_TOKEN,
        STUB_TOKEN_EXPIRED,
        STUB_TOKEN_REFRESH_1,
        STUB_LAST_REQUEST_BLOG_LIST,
        STUB_CURRENT_BLOG_ID
    )

    @Test
    fun test() {
        val mockBloggerApiDataSource = mock<DataSource.IBloggerApiDataSource> {
        }
        val preferenceDataSource = mock<DataSource.IPreferenceDataSource> {
            on {
                getAccounts()
            } doReturn (list)
        }
        val test = AccountRepository(mockBloggerApiDataSource, preferenceDataSource)
        val ret = test.getAllAccounts()
        Assert.assertEquals(list, ret)
    }

    @Test
    fun setCurrentAccount() {
        val mockBloggerApiDataSource = mock<DataSource.IBloggerApiDataSource> {
        }
        val preferenceDataSource = mock<DataSource.IPreferenceDataSource> {
        }
        val test = AccountRepository(mockBloggerApiDataSource, preferenceDataSource)
        test.setCurrentAccount(stub)
        verify(preferenceDataSource).saveCurrentAccount(stub)
    }

    @Test
    fun updateLastBlogListRequest() {
        val mockBloggerApiDataSource = mock<DataSource.IBloggerApiDataSource> {
        }
        val preferenceDataSource = mock<DataSource.IPreferenceDataSource> {
        }
        val test = AccountRepository(mockBloggerApiDataSource, preferenceDataSource)
        test.updateLastBlogListRequest(stub, STUB_TOKEN_EXPIRED)
        verify(preferenceDataSource).saveAccount(stub, STUB_TOKEN_EXPIRED)
    }

    @Test
    fun getCurrentAccount() {
        val mockBloggerApiDataSource = mock<DataSource.IBloggerApiDataSource> {
        }
        val preferenceDataSource = mock<DataSource.IPreferenceDataSource> {
            on {
                getCurrentAccount()
            } doReturn (stub)
        }
        val test = AccountRepository(mockBloggerApiDataSource, preferenceDataSource)
        val ret = test.getCurrentAccount()
        Assert.assertEquals(stub, ret)
    }

    @Test
    fun getCurrentAccount2() {
        val mockBloggerApiDataSource = mock<DataSource.IBloggerApiDataSource> {
        }
        val preferenceDataSource = mock<DataSource.IPreferenceDataSource> {
            on {
                getAccounts()
            } doReturn (list)
        }
        val test = AccountRepository(mockBloggerApiDataSource, preferenceDataSource)
        val ret = test.getCurrentAccount()
        Assert.assertEquals(STUB_USER_ID_1, ret.getId())
    }

    @Test
    fun getRefreshToken() {
        val mockBloggerApiDataSource = mock<DataSource.IBloggerApiDataSource> {
        }
        val preferenceDataSource = mock<DataSource.IPreferenceDataSource> {
            on {
                getAccount(STUB_USER_ID)
            } doReturn (stub)
        }
        val test = AccountRepository(mockBloggerApiDataSource, preferenceDataSource)
        val ret = test.getRefreshToken(STUB_USER_ID)
        Assert.assertEquals(ret, stub.getRefreshToken())
    }

    @Test
    fun getAccessToken() {
        val mockBloggerApiDataSource = mock<DataSource.IBloggerApiDataSource> {
        }
        val preferenceDataSource = mock<DataSource.IPreferenceDataSource> {
            on {
                getAccount(STUB_USER_ID)
            } doReturn (stub)
        }
        val test = AccountRepository(mockBloggerApiDataSource, preferenceDataSource)
        test.getAccessToken(STUB_USER_ID, 0).test().assertNoErrors().assertValue(STUB_ACCESS_TOKEN)
    }

    @Test
    fun getAccessToken2() {
        val mockBloggerApiDataSource = mock<DataSource.IBloggerApiDataSource> {
        }
        val preferenceDataSource = mock<DataSource.IPreferenceDataSource> {
            on {
                getAccount(STUB_USER_ID)
            } doReturn (stub)
        }
        val test = AccountRepository(mockBloggerApiDataSource, preferenceDataSource)
        test.getAccessToken(STUB_USER_ID, STUB_NOW).test().assertError(Exception::class.java)
    }

    @Test
    fun requestAccessToken() {
        val response = OauthResponse()
        val mockBloggerApiDataSource = mock<DataSource.IBloggerApiDataSource> {
            on {
                requestAccessToken(STUB_SERVER_AUTH_CODE)
            } doReturn (Single.create { emitter -> emitter.onSuccess(response) })
        }
        val preferenceDataSource = mock<DataSource.IPreferenceDataSource> {
            on {
                getAccount(STUB_USER_ID)
            } doReturn (stub)
        }
        val test = AccountRepository(mockBloggerApiDataSource, preferenceDataSource)
        test.requestAccessToken(STUB_SERVER_AUTH_CODE).test().assertNoErrors().assertValue(response)
    }

    @Test
    fun requestRefresh() {
        // 面倒
        val response = OauthResponse()
        response.access_token = STUB_ACCESS_TOKEN
        response.expires_in = 3600

        val mockBloggerApiDataSource = mock<DataSource.IBloggerApiDataSource> {
            on {
                refreshAccessToken(STUB_TOKEN_REFRESH_1)
            } doReturn (Single.create { emitter -> emitter.onSuccess(response) })
        }
        val preferenceDataSource = mock<DataSource.IPreferenceDataSource> {
        }
        val test = AccountRepository(mockBloggerApiDataSource, preferenceDataSource)
        test.requestRefresh(STUB_USER_ID, STUB_TOKEN_REFRESH_1, STUB_NOW).test().assertNoErrors()
            .assertComplete()
        verify(preferenceDataSource).saveAccessToken(
            STUB_USER_ID,
            STUB_ACCESS_TOKEN,
            STUB_TOKEN_REFRESH_1,
            STUB_NOW + 3600 * 1000L
        )
    }

    @Test
    fun saveNewAccount() {
        // todo:
    }
}