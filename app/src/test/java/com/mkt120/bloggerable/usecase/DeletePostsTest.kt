package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.repository.Repository
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Test

class DeletePostsTest {

    companion object {
        private const val STUB_USER_ID = "stubUserId"
        private const val STUB_ACCESS_TOKEN = "stubAccessToken"
        private const val STUB_BLOG_ID = "stubBlogId"
        private const val STUB_POST_ID = "stubPostId"
        private val STUB_NOW = System.currentTimeMillis()
    }

    private val mockGetAccessToken = mock<UseCase.IGetAccessToken> {
        on {
            execute(STUB_USER_ID, STUB_NOW)
        } doReturn (Single.create { emitter -> emitter.onSuccess(STUB_ACCESS_TOKEN) })
    }

    @Test
    fun testExecute() {
        val mockRepository = mock<Repository.IPostsRepository> {
            on {
                deletePosts(
                    STUB_ACCESS_TOKEN,
                    STUB_BLOG_ID,
                    STUB_POST_ID
                )
            } doReturn (Completable.create { emitter -> emitter.onComplete() })

            on {
                deletePosts(STUB_BLOG_ID, STUB_POST_ID)
            } doReturn (Completable.create { emitter -> emitter.onComplete() })
        }
        val deletePosts = DeletePosts(mockGetAccessToken, mockRepository)
        deletePosts.execute(STUB_USER_ID, STUB_BLOG_ID, STUB_POST_ID, STUB_NOW).test()
            .assertComplete()
    }

    @Test
    fun testExecute2() {
        val mockRepository = mock<Repository.IPostsRepository> {
            on {
                deletePosts(
                    STUB_ACCESS_TOKEN,
                    STUB_BLOG_ID,
                    STUB_POST_ID
                )
            } doReturn (Completable.create { emitter -> emitter.onError(Exception()) })

            on {
                deletePosts(STUB_BLOG_ID, STUB_POST_ID)
            } doReturn (Completable.create { emitter -> emitter.onComplete() })
        }
        val deletePosts = DeletePosts(mockGetAccessToken, mockRepository)
        deletePosts.execute(STUB_USER_ID, STUB_BLOG_ID, STUB_POST_ID, STUB_NOW).test()
            .assertError(Exception::class.java)
    }
}