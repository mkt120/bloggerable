package com.mkt120.bloggerable.util

import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.model.posts.Posts
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where

class RealmManager(private val realm: Realm) {

    fun saveBlog(blog: Blogs) {
        val list = mutableListOf<Blogs>()
        list.add(blog)
        saveAllBlogs(list)
    }

    fun saveAllBlogs(blogsList: List<Blogs>) {
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

    fun findAllBlogs(userId: String): MutableList<Blogs> {
        val blogsList = realm.where<Blogs>().findAll()
        if (blogsList != null) {
            return realm.copyFromRealm(blogsList)
        }
        return mutableListOf()
    }

    fun findAllPosts(blogsId: String?, isPost: Boolean): List<Posts> {
        if (blogsId != null) {
            val list =
                realm.where<Posts>().equalTo("blog.id", blogsId).equalTo("isPost", isPost).sort("published", Sort.DESCENDING).findAll()
            if (list != null) {
                return realm.copyFromRealm(list)
            }
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

    fun findAllLabels(blogsId: String): ArrayList<String> {
        val posts = realm.where<Posts>().equalTo("blog.id", blogsId).findAll()
        val labels = mutableListOf<String>()
        for (post in posts) {
            labels.addAll(post.labels!!)
        }
        return ArrayList<String>(labels.toHashSet())
    }

    fun deletePosts(blogsId: String, postsId: String) {
        realm.executeTransaction {
            realm.where<Posts>().equalTo("blog.id", blogsId).equalTo("id", postsId).findFirst()
                ?.deleteFromRealm()
        }
    }

}