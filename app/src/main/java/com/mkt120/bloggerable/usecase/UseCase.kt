package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.model.Account
import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.model.posts.Posts
import io.reactivex.Observable
import io.reactivex.Single

interface UseCase {
    interface IGetCurrentAccount {
        fun execute(): Account?
    }

    interface IGetAccessToken {
        fun execute(userId: String): Single<String>
    }

    interface ISaveCurrentAccount {
        fun execute(account: Account)
    }

    interface IFindAllBlog {
        fun execute(userId: String): Single<MutableList<Blogs>>
    }

    interface IGetAllPosts {
        fun execute(
            account: Account,
            blog: Blogs
        ): Observable<Pair<List<Posts>, List<Posts>>>
    }

    interface IGetLabels {
        fun execute(blogId: String): List<String>
    }

    interface ICreateBackupFile {
        fun execute(blogId: String, postId: String? = null, title: String, content: String)
    }

    interface IReadBackupFile {
        fun execute(blogId: String, postId: String? = null): Posts?
    }

    interface IDeleteBackupFile {
        fun execute(blogId: String, postId: String?)
    }


}