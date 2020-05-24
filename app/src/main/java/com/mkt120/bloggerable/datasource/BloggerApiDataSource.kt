package com.mkt120.bloggerable.datasource

import com.mkt120.bloggerable.ApiManager
import com.mkt120.bloggerable.api.BlogsResponse
import com.mkt120.bloggerable.api.OauthResponse
import com.mkt120.bloggerable.model.posts.Posts
import io.reactivex.Completable
import io.reactivex.Single

class BloggerApiDataSource : DataSource.IBloggerApiDataSource {

    /**
     * アクセストークンの取得
     */
    override fun requestAccessToken(
        serverAuthCode: String
    ): Single<OauthResponse> = ApiManager.requestAccessToken(serverAuthCode, "")

    /**
     * トークンのリフレッシュ要求
     */
    override fun refreshAccessToken(refreshToken: String): Single<OauthResponse> =
        ApiManager.refreshToken("", refreshToken)


    override fun requestPostsList(
        accessToken: String,
        blogId: String
    ): Single<Pair<List<Posts>?, Boolean>> =
        ApiManager.getPosts(accessToken, blogId)

    override fun requestDraftPostsList(
        accessToken: String,
        blogId: String
    ): Single<Pair<List<Posts>?, Boolean>> = ApiManager.getDraftPosts(accessToken, blogId)

    override fun updatePosts(
        accessToken: String,
        posts: Posts
    ): Completable {
        return ApiManager.updatePosts(accessToken, posts)
    }

    override fun deletePosts(
        accessToken: String,
        blogId: String,
        id: String
    ): Completable = ApiManager.deletePosts(accessToken, blogId, id)

    override fun publishPosts(
        accessToken: String,
        blogId: String,
        postId: String
    ): Completable = ApiManager.publishPosts(accessToken, blogId, postId)

    override fun revertPosts(
        accessToken: String,
        blogId: String,
        postsId: String
    ): Completable = ApiManager.revertPosts(accessToken, blogId, postsId)

    override fun createPosts(
        accessToken: String,
        blogId: String,
        title: String,
        html: String,
        labels: Array<String>?,
        draft: Boolean
    ): Completable = ApiManager.createPosts(accessToken, blogId, title, html, labels, draft)

    override fun getBlogs(accessToken: String): Single<BlogsResponse> =
        ApiManager.getBlogs(accessToken)

}