package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.repository.Repository
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Test
import java.lang.Exception

class CreatePostsTest {
    companion object {
        private const val STUB_USER_ID = "stubUserId"
        private const val STUB_ACCESS_TOKEN = "stubAccessToken"
        private const val STUB_BLOG_ID = "stubBlogId"
        private const val STUB_TITLE = "stubTitle"
        private const val STUB_HTML = "stubHtml"
        private val STUB_NOW = System.currentTimeMillis()
    }

    private val mockGetAccessToken = mock<UseCase.IGetAccessToken> {
        on {
            execute(STUB_USER_ID, STUB_NOW)
        } doReturn (Single.create { emitter -> emitter.onSuccess(STUB_ACCESS_TOKEN) })
    }

    @Test
    fun testExecuteSuccess() {
        val mockRepository = mock<Repository.IPostsRepository> {
            on {
                createPosts(
                    STUB_ACCESS_TOKEN,
                    STUB_BLOG_ID,
                    STUB_TITLE,
                    STUB_HTML,
                    null,
                    true
                )
            } doReturn (Completable.create { emitter -> emitter.onComplete() })
        }
        val createPosts = CreatePosts(mockGetAccessToken, mockRepository)
        createPosts.execute(STUB_USER_ID, STUB_BLOG_ID, STUB_TITLE, STUB_HTML, null, true, STUB_NOW)
            .test().assertComplete()
    }

    @Test
    fun testExecuteError() {
        val mockRepository = mock<Repository.IPostsRepository> {
            on {
                createPosts(
                    STUB_ACCESS_TOKEN,
                    STUB_BLOG_ID,
                    STUB_TITLE,
                    STUB_HTML,
                    null,
                    true
                )
            } doReturn (Completable.create { emitter -> emitter.onError(Exception()) })
        }
        val createPosts = CreatePosts(mockGetAccessToken, mockRepository)
        createPosts.execute(STUB_USER_ID, STUB_BLOG_ID, STUB_TITLE, STUB_HTML, null, true, STUB_NOW)
            .test().assertError(Exception::class.java)
    }

}