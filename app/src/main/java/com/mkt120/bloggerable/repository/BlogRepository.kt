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
        realmDataSource.addAllBlogs(blogList)
    }

    fun requestAllBlog(accessToken: String, listener: ApiManager.BlogListener) {
        bloggerApiDataSource.getBlogs(accessToken, listener)
    }

    fun findAllLabels(blogId: String): ArrayList<String> = realmDataSource.findAllLabels(blogId)
}
