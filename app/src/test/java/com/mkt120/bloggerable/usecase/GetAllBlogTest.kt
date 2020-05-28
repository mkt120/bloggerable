package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.model.Account
import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.repository.Repository
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Single
import org.junit.Test

class GetAllBlogTest {
    companion object {
        private const val STUB_BLOG_ID = "stubBlogId"
        private const val STUB_USER_ID = "stubUserId"
        private const val STUB_ACCESS_TOKEN = "stubAccessToken"
        private val STUB_NOW = System.currentTimeMillis()

        private const val STUB_USER_NAME = "stubUserName"
        private const val STUB_USER_URL = "stubUserUrl"
        private const val STUB_TOKEN_EXPIRED = 1000L
        private const val STUB_TOKEN_REFRESH = "stubTokenRefresh"
        private const val STUB_LAST_REQUEST_BLOG_LIST = 1000L
    }

    private val stubAccount =
        Account(
            STUB_USER_ID,
            STUB_USER_NAME,
            STUB_USER_URL,
            STUB_ACCESS_TOKEN,
            STUB_TOKEN_EXPIRED,
            STUB_TOKEN_REFRESH,
            STUB_LAST_REQUEST_BLOG_LIST,
            STUB_BLOG_ID

        )

    private val mockGetAccessToken = mock<UseCase.IGetAccessToken> {
        on {
            execute(STUB_USER_ID)
        } doReturn (Single.create { emitter -> emitter.onSuccess(STUB_ACCESS_TOKEN) })
    }
    private val mockAccountRepository = mock<Repository.IAccountRepository> {
        // Preferenceから取得
        on {
            getAccessToken(
                STUB_USER_ID,
                STUB_NOW
            )
            // トークンが有効期限切れであれば、onErrorが発生する
        } doReturn (Single.create { emitter -> emitter.onSuccess(STUB_ACCESS_TOKEN) })
    }

    @Test
    fun execute() {
        val stubRet = mutableListOf<Blogs>()
        val mockRepository = mock<Repository.IBlogRepository> {
            on {
                requestAllBlog(STUB_ACCESS_TOKEN)
            } doReturn (Single.create { emitter -> emitter.onSuccess(stubRet) })
        }
        val timeRepository = mock<Repository.ITimeRepository> {
            on {
                getCurrentTime()
            } doReturn (STUB_NOW)
        }

        val getAllBlog =
            GetAllBlog(mockGetAccessToken, mockAccountRepository, mockRepository, timeRepository)
        getAllBlog.execute(stubAccount).test().assertNoErrors().assertComplete()
        verify(mockRepository).saveAllBlog(check { it ->
            assert(it.isEmpty())
        })
        verify(mockAccountRepository, times(0)).updateLastBlogListRequest(stubAccount, STUB_NOW)
    }

    @Test
    fun execute2() {
        val stubRet = mutableListOf<Blogs>()
        stubRet.add(Blogs())
        val mockRepository = mock<Repository.IBlogRepository> {
            on {
                requestAllBlog(STUB_ACCESS_TOKEN)
            } doReturn (Single.create { emitter -> emitter.onSuccess(stubRet) })
        }
        val timeRepository = mock<Repository.ITimeRepository> {
            on {
                getCurrentTime()
            } doReturn (STUB_NOW)
        }

        val getAllBlog =
            GetAllBlog(mockGetAccessToken, mockAccountRepository, mockRepository, timeRepository)
        getAllBlog.execute(stubAccount).test().assertNoErrors().assertComplete()
        verify(mockRepository).saveAllBlog(check { it ->
            assert(it.size == 1)
        })
        verify(mockAccountRepository, times(1)).updateLastBlogListRequest(stubAccount, STUB_NOW)
    }
}
