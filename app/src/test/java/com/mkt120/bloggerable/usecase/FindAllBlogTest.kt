package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.repository.Repository
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.Single
import org.junit.Test

class FindAllBlogTest {
    companion object {
        private const val STUB_USER_ID = "stubUserId"
    }

    @Test
    fun execute() {
        val ret = mutableListOf<Blogs>()
        val blogRepository = mock<Repository.IBlogRepository> {
            on {
                findAllBlog(STUB_USER_ID)
            } doReturn (Single.create { emitter ->
                emitter.onSuccess(ret)
            })
        }
        val findAllBlog = FindAllBlog(blogRepository)
        findAllBlog.execute(STUB_USER_ID).test().assertValue(ret)
    }
}