package com.mkt120.bloggerable.datasource

import android.content.Intent
import com.mkt120.bloggerable.api.BlogsResponse
import com.mkt120.bloggerable.api.UserInfoResponse
import com.mkt120.bloggerable.model.Account
import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.model.posts.Posts
import io.reactivex.Completable
import io.reactivex.Single
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.TokenResponse

interface DataSource {
    interface IBloggerApiDataSource {
        fun requestUserInfo(accessToken: String): Single<UserInfoResponse>

        fun requestPostsList(
            accessToken: String,
            blogId: String
        ): Single<List<Posts>>

        fun requestDraftPostsList(
            accessToken: String,
            blogId: String
        ): Single<List<Posts>>

        fun updatePosts(accessToken: String, old: Posts): Completable

        fun deletePosts(
            accessToken: String,
            blogId: String,
            postId: String
        ): Completable

        fun publishPosts(
            accessToken: String,
            blogId: String,
            postId: String
        ): Completable

        fun revertPosts(
            accessToken: String,
            blogId: String,
            postId: String
        ): Completable

        fun createPosts(
            accessToken: String,
            blogId: String,
            title: String,
            html: String,
            labels: Array<String>?,
            isDraft: Boolean
        ): Completable

        fun getBlogs(accessToken: String): Single<BlogsResponse>
    }

    interface IGoogleOauthApiDataSource {
        fun getAuthorizeIntent(): Intent

        fun requestAccessToken(
            response: AuthorizationResponse,
            onResponse: (accessToken: String, refreshToken: String, tokenExpired: Long) -> Unit,
            onFailed: (exception: Throwable) -> Unit
        )

        fun refreshAccessToken(
            refreshToken: String,
            onResponse: (TokenResponse) -> Unit,
            onFailed: (exception: Throwable) -> Unit
        )
    }

    interface IPreferenceDataSource {
        fun getCurrentAccount(): Account?
        fun saveCurrentAccount(account: Account)
        fun updateLastBlogListRequest(account: Account, lastBlogListRequest: Long)
        fun addNewAccount(
            id: String,
            name: String,
            photoUrl: String,
            accessToken: String,
            tokenExpiredDateMillis: Long,
            refreshToken: String
        ): Account

        fun updateAccessToken(
            id: String,
            accessToken: String,
            refreshToken: String,
            expired: Long
        )

        fun getAccounts(): ArrayList<Account>
        fun getAccount(id: String): Account?
    }

    interface IRealmDataSource {
        fun saveBlogs(blogs: Blogs)
        fun saveAllBlogs(blogsList: List<Blogs>)
        fun savePosts(posts: List<Posts>, isDraft: Boolean)
        fun findAllPost(blogId: String?, isPost: Boolean): Single<List<Posts>>
        fun findPosts(blogId: String, postsId: String): Single<Posts>
        fun deletePosts(blogId: String, postsId: String): Completable
        fun findAllBlogs(id: String): Single<MutableList<Blogs>>
        fun findAllLabels(blogId: String): List<String>

    }

}