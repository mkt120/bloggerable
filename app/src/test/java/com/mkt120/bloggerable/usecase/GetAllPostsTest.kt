package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.model.posts.Posts
import com.mkt120.bloggerable.presenter.TopPresenterTest
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
        private const val STUB_ACCESS_TOKEN = "stubAccessToken"
        private val STUB_NOW = System.currentTimeMillis()

        private const val STUB_BLOG_ID = "stubBlogId"
    }

    private val mockGetAccessToken = mock<UseCase.IGetAccessToken> {
        on {
            execute(STUB_USER_ID)
        } doReturn (Single.create { emitter -> emitter.onSuccess(STUB_ACCESS_TOKEN) })
    }
    private val mockBlogsRepository = mock<Repository.IBlogRepository> {
    }

    @Test
    fun execute1() {
        val mockPostsRepository = mock<Repository.IPostsRepository> {
            on {
                requestLivePosts(STUB_ACCESS_TOKEN, STUB_BLOG_ID)
            } doReturn (Single.create { emitter ->
                val list = mutableListOf<Posts>()
                val pair = Pair<List<Posts>, Boolean>(list, false)
                emitter.onSuccess(pair)
            })
            on {
                requestDraftPosts(STUB_ACCESS_TOKEN, STUB_BLOG_ID)
            } doReturn (Single.create { emitter ->
                val list = mutableListOf<Posts>()
                val pair = Pair<List<Posts>, Boolean>(list, true)
                emitter.onSuccess(pair)
            })
        }
        val blogs = Blogs()
        blogs.id = STUB_BLOG_ID
        val timeRepository = mock<Repository.ITimeRepository> {
            on {
                getCurrentTime()
            } doReturn (STUB_NOW)
        }
        val getAllPosts = GetAllPosts(mockGetAccessToken, mockPostsRepository, mockBlogsRepository, timeRepository)
        getAllPosts.execute(STUB_USER_ID, blogs).test().assertNoErrors().assertComplete()
        verify(mockPostsRepository, times(1)).savePosts(mutableListOf(), true)
        verify(mockPostsRepository, times(1)).savePosts(mutableListOf(), false)
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
                val pair = Pair<List<Posts>, Boolean>(livePosts, false)
                emitter.onSuccess(pair)
            })
            on {
                requestDraftPosts(STUB_ACCESS_TOKEN, STUB_BLOG_ID)
            } doReturn (Single.create { emitter ->
                val pair = Pair<List<Posts>, Boolean>(draftPosts, true)
                emitter.onSuccess(pair)
            })
        }
        val blogs = Blogs().apply {
            id = STUB_BLOG_ID
        }
        val timeRepository = mock<Repository.ITimeRepository> {
            on {
                getCurrentTime()
            } doReturn (STUB_NOW)
        }
        val getAllPosts = GetAllPosts(mockGetAccessToken, mockPostsRepository, mockBlogsRepository, timeRepository)
        getAllPosts.execute(STUB_USER_ID, blogs).test().assertNoErrors().assertComplete()

        verify(mockPostsRepository, times(1)).savePosts(draftPosts, true)
        verify(mockPostsRepository, times(1)).savePosts(livePosts, false)

        // todo: 両方とも成功したときに1回だけ呼ばれるべき...
        verify(mockBlogsRepository, times(2)).updateLastPostListRequest(blogs, STUB_NOW)
    }
}