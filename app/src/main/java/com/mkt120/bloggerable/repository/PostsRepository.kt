package com.mkt120.bloggerable.repository

import com.mkt120.bloggerable.ApiManager
import com.mkt120.bloggerable.datasource.BloggerApiDataSource
import com.mkt120.bloggerable.datasource.RealmDataSource
import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.model.posts.Posts

class PostsRepository(
    private val bloggerApiDataSource: BloggerApiDataSource,
    private val realmDataSource: RealmDataSource
) {

    fun requestLivePosts(
        accessToken: String,
        blogId: String,
        postsListener: ApiManager.PostsListener
    ) {
        bloggerApiDataSource.requestPostsList(accessToken, blogId, postsListener)
    }

    fun requestDraftPosts(
        accessToken: String,
        blogId: String,
        postsListener: ApiManager.PostsListener
    ) {
        bloggerApiDataSource.requestDraftPostsList(accessToken, blogId, postsListener)
    }

    fun createPosts(
        accessToken: String,
        blogId: String,
        title: String,
        html: String,
        labels: Array<String>?,
        draft: Boolean,
        completeListener: ApiManager.CompleteListener
    ) {
        bloggerApiDataSource.createPosts(
            accessToken,
            blogId,
            title,
            html,
            labels,
            draft,
            completeListener
        )
    }

    fun savePosts(posts: List<Posts>, isDraft: Boolean) {
        realmDataSource.savePosts(posts, isDraft)
    }

    fun findPosts(blogId: String, postsId: String): Posts? =
        realmDataSource.findPosts(blogId, postsId)

    fun revertPosts(
        accessToken: String,
        blogId: String,
        postsId: String,
        listener: ApiManager.CompleteListener
    ) {
        bloggerApiDataSource.revertPosts(accessToken, blogId, postsId, listener)
    }

    fun publishPosts(
        accessToken: String,
        blogsId: String,
        postsId: String,
        listener: ApiManager.CompleteListener
    ) {
        bloggerApiDataSource.publishPosts(accessToken, blogsId, postsId, listener)
    }

    fun deletePosts(
        accessToken: String,
        blogId: String,
        postsId: String,
        listener: ApiManager.CompleteListener
    ) {
        bloggerApiDataSource.deletePosts(accessToken, blogId, postsId, listener)
    }

    fun updateLastRequest(blog:Blogs, update:Long) {
        blog.lastRequestPosts = update
        realmDataSource.saveBlogs(blog)
    }
}