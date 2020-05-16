package com.mkt120.bloggerable.util

import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.model.posts.Posts
import io.realm.Realm
import io.realm.kotlin.where

class RealmManager(private val realm: Realm) {

    fun addAllBlogs(blogsList: List<Blogs>) {
        realm.executeTransaction {
            realm.insertOrUpdate(blogsList)
        }
    }

    fun addAllPosts(posts: List<Posts>, isDraft: Boolean) {
        posts.forEach { post -> post.isPost = !isDraft }
        realm.executeTransaction {
            realm.insertOrUpdate(posts)
        }
    }

    fun findAllBlogs(): MutableList<Blogs> {
        val blogsList = realm.where<Blogs>().findAll()
        if (blogsList != null) {
            return realm.copyFromRealm(blogsList)
        }
        return mutableListOf()
    }

    fun findAllPosts(blogsId: String, isPost: Boolean): List<Posts> {
        val list =
            realm.where<Posts>().equalTo("blog.id", blogsId).equalTo("isPost", isPost).findAll()
        if (list != null) {
            return realm.copyFromRealm(list)
        }
        return listOf()
    }

    fun findPosts(blogsId: String, postsId: String): Posts? {
        val posts =
            realm.where<Posts>().equalTo("blog.id", blogsId).equalTo("id", postsId).findFirst()
        if (posts != null) {
            return realm.copyFromRealm(posts)
        }
        return null
    }

}