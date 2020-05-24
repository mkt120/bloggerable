package com.mkt120.bloggerable.repository

import com.mkt120.bloggerable.api.BlogsResponse
import com.mkt120.bloggerable.datasource.DataSource
import com.mkt120.bloggerable.model.blogs.Blogs
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test


class BlogRepositoryTest {
    companion object {
        private const val STUB_USER_ID = "stubUserId"
        private const val STUB_ACCESS_TOKEN = "stubAccessToken"
        private const val STUB_BLOG_ID = "stubBlogId"
        private val STUB_NOW = System.currentTimeMillis()

        private const val STUB_LABEL_1 = "stubLabel1"
        private const val STUB_LABEL_2 = "stubLabel2"
        private const val STUB_LABEL_3 = "stubLabel1"
    }

    @Before
    fun setUp() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler {
            Schedulers.trampoline()
        }
    }

    @After
    fun tearDown() {
        RxAndroidPlugins.reset()
    }

    @Test
    fun findAllBlog() {
        val mockBloggerApiDataSource = mock<DataSource.IBloggerApiDataSource> {
        }
        val mockRealmDataSource = mock<DataSource.IRealmDataSource> {
            on {
                findAllBlogs(STUB_USER_ID)
            } doReturn (Single.create { emitter -> emitter.onSuccess(mutableListOf()) })
        }
        val test = BlogRepository(mockBloggerApiDataSource, mockRealmDataSource)
        test.findAllBlog(STUB_USER_ID).test().assertNoErrors().assertValue(mutableListOf())
    }

    @Test
    fun findAllBlog2() {
        val mockBloggerApiDataSource = mock<DataSource.IBloggerApiDataSource> {
        }
        val ret = mutableListOf<Blogs>()
        ret.add(Blogs())
        val mockRealmDataSource = mock<DataSource.IRealmDataSource> {
            on {
                findAllBlogs(STUB_USER_ID)
            } doReturn (Single.create { emitter -> emitter.onSuccess(ret) })
        }
        val test = BlogRepository(mockBloggerApiDataSource, mockRealmDataSource)
        test.findAllBlog(STUB_USER_ID).test().assertNoErrors().assertValue(ret)
    }

    @Test
    fun saveAllBlog() {
        val mockBloggerApiDataSource = mock<DataSource.IBloggerApiDataSource> {
        }
        val ret = mutableListOf<Blogs>()
        ret.add(Blogs())
        val mockRealmDataSource = mock<DataSource.IRealmDataSource> {
        }
        val test = BlogRepository(mockBloggerApiDataSource, mockRealmDataSource)
        test.saveAllBlog(ret)
        verify(mockRealmDataSource, times(1)).saveAllBlogs(ret)
    }

    @Test
    fun requestAllBlog1() {
        val mockBloggerApiDataSource = mock<DataSource.IBloggerApiDataSource> {
            on {
                getBlogs(STUB_ACCESS_TOKEN)
            } doReturn (Single.create { emitter ->
                emitter.onSuccess(BlogsResponse())
            })
        }
        val mockRealmDataSource = mock<DataSource.IRealmDataSource> {
        }
        val test = BlogRepository(mockBloggerApiDataSource, mockRealmDataSource)
        test.requestAllBlog(STUB_ACCESS_TOKEN).test().await().assertNoValues()
    }

    @Test
    fun requestAllBlog2() {
        val mockBloggerApiDataSource = mock<DataSource.IBloggerApiDataSource> {
            on {
                getBlogs(STUB_ACCESS_TOKEN)
            } doReturn (Single.create { emitter ->
                emitter.onSuccess(BlogsResponse())
            })
        }
        val mockRealmDataSource = mock<DataSource.IRealmDataSource> {
        }
        val test = BlogRepository(mockBloggerApiDataSource, mockRealmDataSource)
        test.requestAllBlog(STUB_ACCESS_TOKEN).test().await().assertNoValues()
    }

    @Test
    fun requestAllBlog3() {
        val list = mutableListOf<Blogs>().apply {
            this.add(Blogs())
        }
        val mockBloggerApiDataSource = mock<DataSource.IBloggerApiDataSource> {
            on {
                getBlogs(STUB_ACCESS_TOKEN)
            } doReturn (Single.create { emitter ->
                val response = BlogsResponse().apply {
                    this.items = list
                }
                emitter.onSuccess(response)
            })
        }
        val mockRealmDataSource = mock<DataSource.IRealmDataSource> {}
        val test = BlogRepository(mockBloggerApiDataSource, mockRealmDataSource)
        test.requestAllBlog(STUB_ACCESS_TOKEN).test().await().assertValue(list)
    }

    @Test
    fun updateLastPostListRequest() {
        val blogs = Blogs()
        val mockBloggerApiDataSource = mock<DataSource.IBloggerApiDataSource> {}
        val mockRealmDataSource = mock<DataSource.IRealmDataSource> {}
        val test = BlogRepository(mockBloggerApiDataSource, mockRealmDataSource)
        test.updateLastPostListRequest(blogs, STUB_NOW)
        verify(mockRealmDataSource).saveBlogs(check {
            it.lastRequestPosts == STUB_NOW
        })
    }

    @Test
    fun findAllLabels() {
        val mockBloggerApiDataSource = mock<DataSource.IBloggerApiDataSource> {}
        val mockRealmDataSource = mock<DataSource.IRealmDataSource> {
            on {
                findAllLabels(STUB_BLOG_ID)
            } doReturn (mutableListOf<String>().apply {
                add(STUB_LABEL_1)
                add(STUB_LABEL_2)
                add(STUB_LABEL_3)
            }.toList())
        }
        val test = BlogRepository(mockBloggerApiDataSource, mockRealmDataSource)
        val ret = test.findAllLabels(STUB_BLOG_ID)
        Assert.assertEquals(3, ret.size)
        Assert.assertEquals(STUB_LABEL_1, ret[0])
        Assert.assertEquals(STUB_LABEL_2, ret[1])
        Assert.assertEquals(STUB_LABEL_3, ret[2])
    }
}