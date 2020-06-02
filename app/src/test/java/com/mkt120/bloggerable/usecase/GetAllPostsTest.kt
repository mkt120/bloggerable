package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.model.Account
import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.model.posts.Posts
import com.mkt120.bloggerable.repository.Repository
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Single
import org.junit.Test

class GetAllPostsTest {
    companion object {
        private const val STUB_USER_ID = "stubUserId"
        private const val STUB_USER_NAME = "stubUserName"
        private const val STUB_USER_URL = "stubUserUrl"
        private val STUB_NOW = System.currentTimeMillis()
        private const val STUB_TOKEN_EXPIRED = 1000L
        private const val STUB_TOKEN_REFRESH_1 = "stubTokenRefresh1"
        private const val STUB_LAST_REQUEST_BLOG_LIST = 0L
        private const val STUB_CURRENT_BLOG_ID = "stubCurrentBlogId"

        private const val STUB_ACCESS_TOKEN = "stubAccessToken"

        private const val STUB_BLOG_ID = "stubBlogId"
    }

    private val mockGetAccessToken = mock<UseCase.IGetAccessToken> {
        on {
            execute(STUB_USER_ID)
        } doReturn (Single.create { emitter -> emitter.onSuccess(STUB_ACCESS_TOKEN) })
    }
    private val mockBlogsRepository = mock<Repository.IBlogRepository> {
    }

    private val account = Account(
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
    fun execute1() {
        val blogs = Blogs()
        blogs.id = STUB_BLOG_ID
        blogs.lastRequestPosts = STUB_NOW

        val mockPostsRepository = mock<Repository.IPostsRepository> {
            on {
                findAllPosts(STUB_BLOG_ID, true)
            } doReturn (Single.create { emitter ->
                val list = mutableListOf<Posts>()
                emitter.onSuccess(list.toList())
            })
            on {
                findAllPosts(STUB_BLOG_ID, false)
            } doReturn (Single.create { emitter ->
                val list = mutableListOf<Posts>()
                emitter.onSuccess(list.toList())
            })
        }
        val timeRepository = mock<Repository.ITimeRepository> {
            on {
                getCurrentTime()
            } doReturn (STUB_NOW)
        }
        val getAllPosts = GetAllPosts(mockGetAccessToken, mockPostsRepository, mockBlogsRepository, timeRepository)
        getAllPosts.execute(account, blogs).test().await().assertNoErrors().assertComplete()
        verify(mockGetAccessToken, times(0)).execute(STUB_USER_ID)
        verify(mockPostsRepository, times(0)).savePosts(mutableListOf(), true)
        verify(mockPostsRepository, times(0)).savePosts(mutableListOf(), false)
        verify(mockBlogsRepository, times(0)).updateLastPostListRequest(blogs, STUB_NOW)
    }

    @Test
    fun execute2() {
        val livePosts = mutableListOf<Posts>()
        livePosts.add(Posts())
        val draftPosts = mutableListOf<Posts>()
        draftPosts.add(Posts())
        val mockPostsRepository = mock<Repository.IPostsRepository> {
            on {
                requestLivePosts(STUB_ACCESS_TOKEN, STUB_BLOG_ID)
            } doReturn (Single.create { emitter ->
                emitter.onSuccess(livePosts)
            })
            on {
                requestDraftPosts(STUB_ACCESS_TOKEN, STUB_BLOG_ID)
            } doReturn (Single.create { emitter ->
                emitter.onSuccess(draftPosts)
            })
        }
        val blogs = Blogs().apply {
            id = STUB_BLOG_ID
            lastRequestPosts = STUB_NOW
        }
        val timeRepository = mock<Repository.ITimeRepository> {
            on {
                getCurrentTime()
            } doReturn (STUB_NOW + 24 * 60 * 60 * 1000L)
        }
        val getAllPosts = GetAllPosts(mockGetAccessToken, mockPostsRepository, mockBlogsRepository, timeRepository)
        getAllPosts.execute(account, blogs).test().assertNoErrors().assertComplete()

        verify(mockPostsRepository, times(1)).savePosts(draftPosts, true)
        verify(mockPostsRepository, times(1)).savePosts(livePosts, false)

        verify(mockBlogsRepository, times(1)).updateLastPostListRequest(blogs, STUB_NOW + 24 * 60 * 60 * 1000L)
    }
}