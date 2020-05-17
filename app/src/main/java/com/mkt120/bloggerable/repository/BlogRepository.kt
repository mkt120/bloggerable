package com.mkt120.bloggerable.repository

import com.mkt120.bloggerable.datasource.BloggerApiDataSource
import com.mkt120.bloggerable.datasource.RealmDataSource
import com.mkt120.bloggerable.model.blogs.Blogs
import io.reactivex.Single

class BlogRepository(
    private val bloggerApiDataSource: BloggerApiDataSource,
    private val realmDataSource: RealmDataSource
) {

    fun findAllBlog(userId: String): List<Blogs> = realmDataSource.findAllBlogs(userId)

    fun saveAllBlog(blogList: List<Blogs>) {
        realmDataSource.saveAllBlogs(blogList)
    }

    fun requestAllBlog(
        accessToken: String
    ): Single<List<Blogs>?> {
        return bloggerApiDataSource.getBlogs(accessToken).map { response -> response.items }
    }

    fun updateLastPostListRequest(blog: Blogs, now: Long) {
        blog.updateLastRequest(now)
        realmDataSource.saveBlogs(blog)
    }

    fun findAllLabels(blogId: String): ArrayList<String> = realmDataSource.findAllLabels(blogId)
}
