package com.mkt120.bloggerable.repository

import com.mkt120.bloggerable.ApiManager
import com.mkt120.bloggerable.datasource.BloggerApiDataSource
import com.mkt120.bloggerable.datasource.RealmDataSource
import com.mkt120.bloggerable.model.blogs.Blogs

class BlogsRepository(
    private val bloggerApiDataSource: BloggerApiDataSource,
    private val realmDataSource: RealmDataSource
) {
    fun findAllBlogs(): List<Blogs> = realmDataSource.findAllBlogs()

    fun saveAllBlogs(blogs: List<Blogs>) {
        realmDataSource.saveBlog(blogs)
    }

    fun requestAllBlogs(accessToken: String, listener: ApiManager.BlogListener) {
        bloggerApiDataSource.getBlogs(accessToken, listener)
    }

}
