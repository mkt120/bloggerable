package com.mkt120.bloggerable.repository

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.mkt120.bloggerable.api.OauthResponse
import com.mkt120.bloggerable.model.Account
import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.model.posts.Posts
import io.reactivex.Completable
import io.reactivex.Single

interface Repository {
    interface IAccountRepository {
        fun getAllAccounts(): ArrayList<Account>
        fun setCurrentAccount(account: Account)
        fun updateLastBlogListRequest(account: Account, now: Long)
        fun getCurrentAccount(): Account
        fun getRefreshToken(id: String): String?
        fun getAccessToken(id: String, now: Long): Single<String>
        fun requestAccessToken(serverAuthCode: String): Single<OauthResponse>
        fun requestRefresh(userId: String, refreshToken: String): Single<String>
        fun saveNewAccount(
            account: GoogleSignInAccount,
            accessToken: String,
            expired: Long,
            refreshToken: String
        ): Account
    }

    interface IPostsRepository {
        fun requestLivePosts(
            accessToken: String,
            blogId: String
        ): Single<Pair<List<Posts>?, Boolean>>

        fun requestDraftPosts(
            accessToken: String,
            blogId: String
        ): Single<Pair<List<Posts>?, Boolean>>

        fun createPosts(
            accessToken: String,
            blogId: String,
            title: String,
            html: String,
            labels: Array<String>?,
            draft: Boolean
        ): Completable

        fun savePosts(posts: List<Posts>, isDraft: Boolean)
        fun findAllPosts(blogId: String?, isPost: Boolean): List<Posts>
        fun findPosts(blogId: String, postsId: String): Posts?
        fun revertPosts(
            accessToken: String,
            blogId: String,
            postsId: String
        ): Completable

        fun updatePosts(
            accessToken: String,
            posts: Posts
        ): Completable

        fun publishPosts(
            accessToken: String,
            blogsId: String,
            postsId: String
        ): Completable

        fun deletePosts(
            accessToken: String,
            blogId: String,
            postsId: String
        ): Completable

        fun deletePosts(
            blogId: String,
            postsId: String
        )
    }

    interface IBlogRepository {
        fun findAllBlog(userId: String): List<Blogs>
        fun saveAllBlog(blogList: List<Blogs>)
        fun requestAllBlog(
            accessToken: String
        ): Single<List<Blogs>?>

        fun updateLastPostListRequest(blog: Blogs, now: Long)
        fun findAllLabels(blogId: String): ArrayList<String>
    }

    interface IGoogleAccountRepository {
        fun getSignInIntent(): Intent
        fun getAccounts(): ArrayList<Account>
    }

}