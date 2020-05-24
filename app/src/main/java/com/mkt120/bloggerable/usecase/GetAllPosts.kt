package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.model.posts.Posts
import com.mkt120.bloggerable.repository.BlogRepository
import com.mkt120.bloggerable.repository.PostsRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class GetAllPosts(
    private val getAccessToken: UseCase.IGetAccessToken,
    private val postsRepository: PostsRepository,
    private val blogRepository: BlogRepository
) {
    fun execute(
        now: Long,
        userId: String,
        blog: Blogs,
        onComplete: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        getAccessToken.execute(userId, System.currentTimeMillis()).toObservable()
            .flatMap { accessToken -> requestAllPosts(blog, accessToken) }.subscribe({ pair ->
                val items = pair.first
                items?.let {
                    postsRepository.savePosts(it, pair.second)
                    blogRepository.updateLastPostListRequest(blog, now)
                }
            }, onError, onComplete)
    }

    private fun requestAllPosts(
        blog: Blogs,
        accessToken: String
    ): Observable<Pair<List<Posts>?, Boolean>> {
        val live = postsRepository.requestLivePosts(accessToken, blog.id!!)
        val draft = postsRepository.requestDraftPosts(accessToken, blog.id!!)
        return Observable.merge(live.toObservable(), draft.toObservable())
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
}
