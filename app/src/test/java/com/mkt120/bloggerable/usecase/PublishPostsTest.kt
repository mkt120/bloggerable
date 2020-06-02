package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.repository.Repository
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Test

class PublishPostsTest {
    companion object {
        private const val STUB_USER_ID = "stubUserId"
        private const val STUB_ACCESS_TOKEN = "stubAccessToken"
        private val STUB_NOW = System.currentTimeMillis()

        private const val STUB_BLOG_ID = "stubBlogId"
        private const val STUB_POSTS_ID = "stubPostsId"
    }

    private val mockGetAccessToken = mock<UseCase.IGetAccessToken> {
        on {
            execute(STUB_USER_ID)
        } doReturn (Single.create { emitter -> emitter.onSuccess(STUB_ACCESS_TOKEN) })
    }
    private val mockRepository = mock<Repository.IPostsRepository> {
        on {
            publishPosts(STUB_ACCESS_TOKEN, STUB_BLOG_ID, STUB_POSTS_ID)
        } doReturn (Completable.create { emitter -> emitter.onComplete() })
    }

    @Test
    fun execute() {
        val publishPosts = PublishPosts(mockGetAccessToken, mockRepository)
        publishPosts.execute(STUB_NOW, STUB_USER_ID, STUB_BLOG_ID, STUB_POSTS_ID).test().assertComplete()
    }
}