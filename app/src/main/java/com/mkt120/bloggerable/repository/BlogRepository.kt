package com.mkt120.bloggerable.repository

import com.mkt120.bloggerable.datasource.BloggerApiDataSource
import com.mkt120.bloggerable.datasource.RealmDataSource
import com.mkt120.bloggerable.model.blogs.Blogs
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class BlogRepository(
    private val bloggerApiDataSource: BloggerApiDataSource,
    private val realmDataSource: RealmDataSource
) : Repository.IBlogRepository {

    override fun findAllBlog(userId: String): Single<MutableList<Blogs>> =
        realmDataSource.findAllBlogs(userId)

    override fun saveAllBlog(blogList: List<Blogs>) {
        realmDataSource.saveAllBlogs(blogList)
    }

    override fun requestAllBlog(
        accessToken: String
    ): Single<List<Blogs>?> {
        return bloggerApiDataSource.getBlogs(accessToken).subscribeOn(Schedulers.io()).observeOn(
            AndroidSchedulers.mainThread()
        ).map { response -> response.items }
    }

    override fun updateLastPostListRequest(blog: Blogs, now: Long) {
        blog.updateLastRequest(now)
        realmDataSource.saveBlogs(blog)
    }

    override fun findAllLabels(blogId: String): ArrayList<String> =
        realmDataSource.findAllLabels(blogId)
}
