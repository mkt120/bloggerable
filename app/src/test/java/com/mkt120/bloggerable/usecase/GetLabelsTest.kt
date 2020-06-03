package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.repository.Repository
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GetLabelsTest {

    companion object {
        private const val STUB_BLOG_ID = "stubBlogId"
    }

    private lateinit var blogRepository: Repository.IBlogRepository
    private lateinit var labels: ArrayList<String>

    @Before
    fun setUp() {
        labels = ArrayList<String>()
        blogRepository = mock<Repository.IBlogRepository>() {
            on {
                findAllLabels(STUB_BLOG_ID)
            } doReturn (labels)
        }
    }

    @Test
    fun execute() {
        val getLabels = GetLabels(blogRepository)
        val ret = getLabels.execute(STUB_BLOG_ID)
        Assert.assertEquals(labels, ret)
    }
}