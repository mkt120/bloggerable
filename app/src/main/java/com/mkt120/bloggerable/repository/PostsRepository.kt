package com.mkt120.bloggerable.repository

import com.mkt120.bloggerable.datasource.BloggerApiDataSource
import com.mkt120.bloggerable.datasource.RealmDataSource
import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.model.posts.Posts
import io.reactivex.Completable
import io.reactivex.Single

class PostsRepository(
    private val bloggerApiDataSource: BloggerApiDataSource,
    private val realmDataSource: RealmDataSource
) {

    fun requestLivePosts(
        accessToken: String,
        blogId: String
    ): Single<Pair<List<Posts>?, Boolean>> {
        return bloggerApiDataSource.requestPostsList(accessToken, blogId)
    }

    fun requestDraftPosts(
        accessToken: String,
        blogId: String
    ): Single<Pair<List<Posts>?, Boolean>> {
        return bloggerApiDataSource.requestDraftPostsList(accessToken, blogId)
    }

    fun createPosts(
        accessToken: String,
        blogId: String,
        title: String,
        html: String,
        labels: Array<String>?,
        draft: Boolean
    ): Completable {
        return bloggerApiDataSource.createPosts(
            accessToken,
            blogId,
            title,
            html,
            labels,
            draft
        )
    }

    fun savePosts(posts: List<Posts>, isDraft: Boolean) {
        realmDataSource.savePosts(posts, isDraft)
    }

    fun findAllPosts(blogId: String?, isPost: Boolean): List<Posts> =
        realmDataSource.findAllPost(blogId, isPost)

    fun findPosts(blogId: String, postsId: String): Posts? =
        realmDataSource.findPosts(blogId, postsId)

    fun revertPosts(
        accessToken: String,
        blogId: String,
        postsId: String
    ) :Completable {
        return bloggerApiDataSource.revertPosts(accessToken, blogId, postsId)
    }

    fun publishPosts(
        accessToken: String,
        blogsId: String,
        postsId: String
    ) :Completable {
        return bloggerApiDataSource.publishPosts(accessToken, blogsId, postsId)
    }

    fun deletePosts(
        accessToken: String,
        blogId: String,
        postsId: String
    ) :Completable {
        return bloggerApiDataSource.deletePosts(accessToken, blogId, postsId)
    }

    fun deletePosts(
        blogId: String,
        postsId: String
    ) {
        realmDataSource.deletePosts(blogId, postsId)
    }

    fun updateLastRequest(blog: Blogs, update: Long) {
        blog.lastRequestPosts = update
        realmDataSource.saveBlogs(blog)
    }
}