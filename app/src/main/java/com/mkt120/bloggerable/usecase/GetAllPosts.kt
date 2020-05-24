package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.model.posts.Posts
import com.mkt120.bloggerable.repository.Repository
import io.reactivex.Completable
import io.reactivex.Observable

class GetAllPosts(
    private val getAccessToken: UseCase.IGetAccessToken,
    private val postsRepository: Repository.IPostsRepository,
    private val blogRepository: Repository.IBlogRepository
) {
    fun execute(
        now: Long,
        userId: String,
        blog: Blogs
    ): Completable = getAccessToken.execute(userId, now)
        .flatMapObservable { accessToken -> requestAllPosts(blog.id!!, accessToken) }
        .flatMapCompletable { pair ->
            Completable.create { emitter ->
                val items = pair.first
                items?.let {
                    postsRepository.savePosts(it, pair.second)
                    if (it.isNotEmpty()) {
                        blogRepository.updateLastPostListRequest(blog, now)
                    }
                }
                emitter.onComplete()
            }
        }

    private fun requestAllPosts(
        blogId: String,
        accessToken: String
    ): Observable<Pair<List<Posts>?, Boolean>> {
        val live = postsRepository.requestLivePosts(accessToken, blogId)
        val draft = postsRepository.requestDraftPosts(accessToken, blogId)
        return Observable.merge(live.toObservable(), draft.toObservable())
    }
}
