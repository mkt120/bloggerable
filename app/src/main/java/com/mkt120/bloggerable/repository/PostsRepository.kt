package com.mkt120.bloggerable.repository

import com.mkt120.bloggerable.datasource.BloggerApiDataSource
import com.mkt120.bloggerable.datasource.RealmDataSource
import com.mkt120.bloggerable.model.posts.Posts
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PostsRepository(
    private val bloggerApiDataSource: BloggerApiDataSource,
    private val realmDataSource: RealmDataSource
) : Repository.IPostsRepository {

    override fun requestLivePosts(
        accessToken: String,
        blogId: String
    ): Single<Pair<List<Posts>?, Boolean>> =
        bloggerApiDataSource.requestPostsList(accessToken, blogId)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())


    override fun requestDraftPosts(
        accessToken: String,
        blogId: String
    ): Single<Pair<List<Posts>?, Boolean>> =
        bloggerApiDataSource.requestDraftPostsList(accessToken, blogId)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    override fun createPosts(
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

    override fun savePosts(posts: List<Posts>, isDraft: Boolean) {
        realmDataSource.savePosts(posts, isDraft)
    }

    override fun findAllPosts(blogId: String?, isPost: Boolean): Single<List<Posts>> =
        realmDataSource.findAllPost(blogId, isPost)

    override fun findPosts(blogId: String, postsId: String): Single<Posts> =
        realmDataSource.findPosts(blogId, postsId)

    override fun revertPosts(
        accessToken: String,
        blogId: String,
        postsId: String
    ): Completable {
        return bloggerApiDataSource.revertPosts(accessToken, blogId, postsId)
    }

    override fun updatePosts(accessToken: String, posts: Posts): Completable {
        return bloggerApiDataSource.updatePosts(accessToken, posts)
    }

    override fun publishPosts(
        accessToken: String,
        blogsId: String,
        postsId: String
    ): Completable {
        return bloggerApiDataSource.publishPosts(accessToken, blogsId, postsId)
    }

    override fun deletePosts(
        accessToken: String,
        blogId: String,
        postsId: String
    ): Completable {
        return bloggerApiDataSource.deletePosts(accessToken, blogId, postsId)
    }

    override fun deletePosts(
        blogId: String,
        postsId: String
    ): Completable = realmDataSource.deletePosts(blogId, postsId)
}