package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.model.posts.Posts
import com.mkt120.bloggerable.repository.Repository
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.Single
import org.junit.Test

class FindPostsTest {
    companion object {
        private const val STUB_BLOG_ID = "stubBlogId"
        private const val STUB_POSTS_ID = "stubPostsId"
    }

    @Test
    fun execute1() {
        val ret = Posts()
        val mockRepository = mock<Repository.IPostsRepository> {
            on {
                findPosts(STUB_BLOG_ID, STUB_POSTS_ID)
            } doReturn (Single.create { emitter -> emitter.onSuccess(ret) })
        }
        val findPosts = FindPosts(mockRepository)
        findPosts.execute(STUB_BLOG_ID, STUB_POSTS_ID).test().assertValue(ret)
    }

    @Test
    fun execute2() {
        val ret = Posts()
        val mockRepository = mock<Repository.IPostsRepository> {
            on {
                findPosts(STUB_BLOG_ID, STUB_POSTS_ID)
            } doReturn (Single.create { emitter -> emitter.onError(Exception()) })
        }
        val findPosts = FindPosts(mockRepository)
        findPosts.execute(STUB_BLOG_ID, STUB_POSTS_ID).test().assertError(Exception::class.java)
    }
}