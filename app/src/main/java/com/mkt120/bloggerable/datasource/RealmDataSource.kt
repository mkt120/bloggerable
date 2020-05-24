package com.mkt120.bloggerable.datasource

import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.model.posts.Posts
import io.reactivex.Completable
import io.reactivex.Single
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where

class RealmDataSource(private val realm: Realm) : DataSource.IRealmDataSource {

    override fun saveBlogs(blogs: Blogs) {
        val list = mutableListOf<Blogs>()
        list.add(blogs)
        saveAllBlogs(list)
    }

    override fun saveAllBlogs(blogsList: List<Blogs>) {
        realm.executeTransaction {
            realm.insertOrUpdate(blogsList)
        }
    }

    override fun savePosts(posts: List<Posts>, isDraft: Boolean) {
        posts.forEach { post -> post.isPost = !isDraft }
        realm.executeTransaction {
            realm.insertOrUpdate(posts)
        }
    }

    override fun findAllPost(blogId: String?, isPost: Boolean): Single<List<Posts>> =
        Single.create { emitter ->
            if (blogId != null) {
                val list =
                    realm.where<Posts>().equalTo("blog.id", blogId).equalTo("isPost", isPost)
                        .sort("published", Sort.DESCENDING).findAll()
                if (list != null) {
                    emitter.onSuccess(realm.copyFromRealm(list))
                }
            }
            emitter.onSuccess(listOf())
        }

    override fun findPosts(blogId: String, postsId: String): Single<Posts> =
        Single.create { emitter ->
            val posts =
                realm.where<Posts>().equalTo("blog.id", blogId).equalTo("id", postsId).findFirst()
            if (posts != null) {
                emitter.onSuccess(realm.copyFromRealm(posts))
            } else {
                emitter.onError(Exception())
            }
        }

    override fun deletePosts(blogId: String, postsId: String): Completable =
        Completable.create { emitter ->
            realm.executeTransaction {
                realm.where<Posts>().equalTo("blog.id", blogId).equalTo("id", postsId).findFirst()
                    ?.deleteFromRealm()
            }
            emitter.onComplete()
        }

    override fun findAllBlogs(id: String): Single<MutableList<Blogs>> = Single.create { emitter ->
        val blogsList = realm.where<Blogs>().findAll()
        if (blogsList != null) {
            emitter.onSuccess(realm.copyFromRealm(blogsList))
        } else {
            emitter.onSuccess(mutableListOf())
        }
    }

    override fun findAllLabels(blogId: String): List<String> {
        val posts = realm.where<Posts>().equalTo("blog.id", blogId).findAll()
        val labels = mutableListOf<String>()
        for (post in posts) {
            labels.addAll(post.labels!!)
        }
        return labels.toHashSet().toList()
    }
}