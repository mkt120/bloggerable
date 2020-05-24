package com.mkt120.bloggerable.util

import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.model.posts.Posts
import io.reactivex.Single
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

    fun findAllBlogs(userId: String): Single<MutableList<Blogs>> =
        Single.create { emitter ->
            val blogsList = realm.where<Blogs>().findAll()
            if (blogsList != null) {
                emitter.onSuccess(realm.copyFromRealm(blogsList))
            } else {
                emitter.onSuccess(mutableListOf())
            }
        }

    fun findAllPosts(blogsId: String?, isPost: Boolean): Single<List<Posts>> =
        Single.create { emitter ->
            if (blogsId != null) {
                val list =
                    realm.where<Posts>().equalTo("blog.id", blogsId).equalTo("isPost", isPost)
                        .sort("published", Sort.DESCENDING).findAll()
                if (list != null) {
                    emitter.onSuccess(realm.copyFromRealm(list))
                }
            }
            emitter.onSuccess(listOf())
        }


    fun findPosts(blogsId: String, postsId: String): Single<Posts> =
        Single.create { emitter ->
            val posts =
                realm.where<Posts>().equalTo("blog.id", blogsId).equalTo("id", postsId).findFirst()
            if (posts != null) {
                emitter.onSuccess(realm.copyFromRealm(posts))
            } else {
                emitter.onError(Exception())
            }
        }

    fun findAllLabels(blogsId: String): ArrayList<String> {
        val posts = realm.where<Posts>().equalTo("blog.id", blogsId).findAll()
        val labels = mutableListOf<String>()
        for (post in posts) {
            labels.addAll(post.labels!!)
        }
        return ArrayList(labels.toHashSet())
    }

    fun deletePosts(blogsId: String, postsId: String) {
        realm.executeTransaction {
            realm.where<Posts>().equalTo("blog.id", blogsId).equalTo("id", postsId).findFirst()
                ?.deleteFromRealm()
        }
    }

}