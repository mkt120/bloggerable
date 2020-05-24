package com.mkt120.bloggerable.datasource

import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.model.posts.Posts
import com.mkt120.bloggerable.util.RealmManager

class RealmDataSource(private val manager: RealmManager) : DataSource.IRealmDataSource {

    override fun saveBlogs(blogs: Blogs) {
        manager.saveBlog(blogs)
    }

    override fun saveAllBlogs(blogsList: List<Blogs>) {
        manager.saveAllBlogs(blogsList)
    }

    override fun savePosts(posts: List<Posts>, isDraft: Boolean) {
        manager.addAllPosts(posts, isDraft)
    }

    override fun findAllPost(blogId: String?, isPost: Boolean): List<Posts> = manager.findAllPosts(blogId, isPost)

    override fun findPosts(blogId: String, postsId: String): Posts? = manager.findPosts(blogId, postsId)

    override fun deletePosts(blogId: String, postsId: String) {
        manager.deletePosts(blogId, postsId)
    }

    override fun findAllBlogs(id: String): List<Blogs> = manager.findAllBlogs(id)

    override fun findAllLabels(blogId: String): ArrayList<String> = manager.findAllLabels(blogId)
}