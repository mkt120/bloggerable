package com.mkt120.bloggerable.repository

import android.content.Intent
import com.mkt120.bloggerable.api.UserInfoResponse
import com.mkt120.bloggerable.model.Account
import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.model.posts.Posts
import io.reactivex.Completable
import io.reactivex.Single
import net.openid.appauth.AuthorizationResponse
import java.io.File

interface Repository {
    interface ITimeRepository {
        fun getCurrentTime(): Long
    }

    interface IAccountRepository {
        fun getAllAccounts(): ArrayList<Account>
        fun setCurrentAccount(account: Account)
        fun updateLastBlogListRequest(account: Account, now: Long)
        fun getCurrentAccount(): Account
        fun getRefreshToken(id: String): String?
        fun getAccessToken(id: String, now: Long): Single<String>
        fun requestUserInfo(accessToken: String): Single<UserInfoResponse>
        fun requestRefresh(
            userId: String,
            refreshToken: String,
            now: Long
        ): Single<String>

        fun saveNewAccount(
            id: String, name: String, photoUrl: String,
            accessToken: String,
            expired: Long,
            refreshToken: String
        ): Account
    }

    interface IPostsRepository {
        fun requestLivePosts(
            accessToken: String,
            blogId: String
        ): Single<List<Posts>>

        fun requestDraftPosts(
            accessToken: String,
            blogId: String
        ): Single<List<Posts>>

        fun createPosts(
            accessToken: String,
            blogId: String,
            title: String,
            html: String,
            labels: Array<String>?,
            draft: Boolean
        ): Completable

        fun savePosts(posts: List<Posts>, isDraft: Boolean)
        fun findAllPosts(blogId: String?, isPost: Boolean): Single<List<Posts>>
        fun findPosts(blogId: String, postsId: String): Single<Posts>
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
        ): Completable
    }

    interface IBlogRepository {
        fun findAllBlog(userId: String): Single<MutableList<Blogs>>
        fun saveAllBlog(blogList: List<Blogs>)
        fun requestAllBlog(
            accessToken: String
        ): Single<List<Blogs>?>

        fun updateLastPostListRequest(blog: Blogs, now: Long)
        fun findAllLabels(blogId: String): List<String>
    }

    interface IGoogleAccountRepository {
        fun getAuthorizeIntent():Intent
        fun getAccounts(): ArrayList<Account>
        fun requestAccessToken(
            response: AuthorizationResponse,
            onResponse: (accessToken: String, refreshToken: String, expired: Long) -> Unit,
            onFailed: (Throwable) -> Unit
        )
    }

    interface IBackupFileRepository {
        fun getFile(fileName: String): File
        fun createFile(fileName: String, title: String, content: String)
        fun deleteFile(fileName: String)
    }

}