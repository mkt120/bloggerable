package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.model.posts.Posts
import com.mkt120.bloggerable.repository.Repository
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.Single
import org.junit.Test

class FindAllPostsTest {
    companion object {
        private const val STUB_BLOG_ID = "stubBlogId"
    }

    @Test
    fun testExecute1() {
        val ret = listOf<Posts>()
        val mockRepository = mock<Repository.IPostsRepository> {
            on {
                findAllPosts(STUB_BLOG_ID, true)
            } doReturn (Single.create { emitter -> emitter.onSuccess(ret) })
        }
        val find = FindAllPosts(mockRepository)
        find.execute(STUB_BLOG_ID, true).test().assertValue(ret)
    }
    @Test
    fun testExecute2() {
        val ret = listOf<Posts>()
        val mockRepository = mock<Repository.IPostsRepository> {
            on {
                findAllPosts(STUB_BLOG_ID, false)
            } doReturn (Single.create { emitter -> emitter.onSuccess(ret) })
        }
        val find = FindAllPosts(mockRepository)
        find.execute(STUB_BLOG_ID, false).test().assertValue(ret)
    }
}