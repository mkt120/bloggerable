package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.model.Account
import com.mkt120.bloggerable.model.blogs.Blogs
import io.reactivex.Completable
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
            userId: String,
            blog: Blogs
        ): Completable
    }
    interface IGetLabels {
        fun execute(blogId: String): List<String>
    }

}