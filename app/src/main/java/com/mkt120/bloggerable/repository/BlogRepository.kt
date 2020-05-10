package com.mkt120.bloggerable.repository

import com.mkt120.bloggerable.ApiManager
import com.mkt120.bloggerable.datasource.BloggerApiDataSource
import com.mkt120.bloggerable.datasource.RealmDataSource
import com.mkt120.bloggerable.model.blogs.Blogs

class BlogRepository(
    private val bloggerApiDataSource: BloggerApiDataSource,
    private val realmDataSource: RealmDataSource
) {

    fun findAllBlog(userId: String): List<Blogs> = realmDataSource.findAllBlogs(userId)

    fun saveAllBlog(blogList: List<Blogs>) {
        realmDataSource.saveAllBlogs(blogList)
    }

    fun requestAllBlog(
        accessToken: String,
        onResponse: (blogList: List<Blogs>?) -> Unit,
        onErrorResponse: (code: Int, message: String) -> Unit,
        onFailed: (t: Throwable) -> Unit
    ) {
        bloggerApiDataSource.getBlogs(accessToken, object :ApiManager.BlogListener {
            override fun onResponse(blogList: List<Blogs>?) {
                onResponse(blogList)
            }

            override fun onErrorResponse(code: Int, message: String) {
                onErrorResponse(code, message)
            }

            override fun onFailed(t: Throwable) {
                onFailed(t)
            }
        })
    }

    fun updateLastPostListRequest(blog: Blogs, now: Long) {
        blog.updateLastRequest(now)
        realmDataSource.saveBlogs(blog)
    }

    fun findAllLabels(blogId: String): ArrayList<String> = realmDataSource.findAllLabels(blogId)
}
