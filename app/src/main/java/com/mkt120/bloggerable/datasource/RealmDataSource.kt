package com.mkt120.bloggerable.datasource

import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.model.posts.Posts
import com.mkt120.bloggerable.util.RealmManager

class RealmDataSource(private val manager: RealmManager) {

    fun saveBlog(blogsList: List<Blogs>) {
        manager.addAllBlogs(blogsList)
    }

    fun savePosts(posts: List<Posts>, isDraft: Boolean) {
        manager.addAllPosts(posts, isDraft)
    }

    fun findPosts(blogId: String, postsId: String): Posts? = manager.findPosts(blogId, postsId)

    fun findAllBlogs(): List<Blogs> = manager.findAllBlogs()

    fun findAllPosts(blogsId: String, isPost: Boolean): List<Posts> =
        manager.findAllPosts(blogsId, isPost)

}