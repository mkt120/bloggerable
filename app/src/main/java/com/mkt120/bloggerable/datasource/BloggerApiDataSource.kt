package com.mkt120.bloggerable.datasource

import com.mkt120.bloggerable.ApiManager
import com.mkt120.bloggerable.model.posts.Posts

class BloggerApiDataSource {

    /**
     * アクセストークンの取得
     */
    fun requestAccessToken(serverAuthCode: String, listener: ApiManager.OauthListener) {
        ApiManager.requestAccessToken(serverAuthCode, "", listener)
    }

    /**
     * トークンのリフレッシュ要求
     */
    fun refreshAccessToken(refreshToken: String, listener: ApiManager.OauthListener) {
        ApiManager.refreshToken("", refreshToken, listener)
    }

    fun requestPostsList(accessToken: String, blogId: String, listener: ApiManager.PostsListener) {
        ApiManager.getPosts(accessToken, blogId, listener)
    }
    fun requestDraftPostsList(accessToken: String, blogId: String, listener: ApiManager.PostsListener) {
        ApiManager.getDraftPosts(accessToken, blogId, listener)
    }

    fun updatePosts(
        accessToken: String,
        posts: Posts,
        completeListener: ApiManager.CompleteListener
    ) {
        ApiManager.updatePosts(accessToken, posts, completeListener)
    }

    fun deletePosts(
        accessToken: String,
        blogId: String,
        id: String,
        completeListener: ApiManager.CompleteListener
    ) {
        ApiManager.deletePosts(accessToken, blogId, id, completeListener)
    }

    fun publishPosts(
        accessToken: String,
        blogId: String,
        postId: String,
        completeListener: ApiManager.CompleteListener
    ) {
        ApiManager.publishPosts(accessToken, blogId, postId, completeListener)
    }

    fun revertPosts(
        accessToken: String,
        blogId: String,
        postsId: String,
        completeListener: ApiManager.CompleteListener
    ) {
        ApiManager.revertPosts(accessToken, blogId, postsId, completeListener)
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
        ApiManager.createPosts(accessToken, blogId, title, html, labels, draft, completeListener)
    }

    fun getBlogs(accessToken: String, listener: ApiManager.BlogListener) {
        ApiManager.getBlogs(accessToken, listener)
    }

}