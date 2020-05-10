package com.mkt120.bloggerable.datasource

import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.model.posts.Posts
import com.mkt120.bloggerable.util.RealmManager

class RealmDataSource(private val manager: RealmManager) {

    fun saveBlogs(blogs: Blogs) {
        manager.saveBlog(blogs)
    }

    fun saveAllBlogs(blogsList: List<Blogs>) {
        manager.saveAllBlogs(blogsList)
    }

    fun savePosts(posts: List<Posts>, isDraft: Boolean) {
        manager.addAllPosts(posts, isDraft)
    }

    fun findPosts(blogId: String, postsId: String): Posts? = manager.findPosts(blogId, postsId)

    fun deletePosts(blogId: String, postsId: String) {
        manager.deletePosts(blogId, postsId)
    }

    fun findAllBlogs(id: String): List<Blogs> = manager.findAllBlogs(id)

    fun findAllLabels(blogId: String): ArrayList<String> = manager.findAllLabels(blogId)
}