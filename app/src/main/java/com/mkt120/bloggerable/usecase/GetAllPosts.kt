package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.model.Account
import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.model.posts.Posts
import com.mkt120.bloggerable.repository.Repository
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

class GetAllPosts(
    private val getAccessToken: UseCase.IGetAccessToken,
    private val postsRepository: Repository.IPostsRepository,
    private val blogRepository: Repository.IBlogRepository,
    private val timeRepository: Repository.ITimeRepository
) : UseCase.IGetAllPosts {

    override fun execute(
        account: Account,
        blog: Blogs
    ): Observable<Pair<List<Posts>, List<Posts>>> {
        return if (blog.isExpired(timeRepository.getCurrentTime())) {
            getAccessToken.execute(account.getId())
                .flatMapObservable { accessToken -> requestAllPosts(blog, accessToken) }
        } else {
            findAllPosts(blog.id!!)
        }
    }

    private fun findAllPosts(
        blogId: String
    ): Observable<Pair<List<Posts>, List<Posts>>> {
        val live = postsRepository.findAllPosts(blogId, true)
        val draft = postsRepository.findAllPosts(blogId, false)
        return Observable.combineLatest(
            live.toObservable(),
            draft.toObservable(),
            BiFunction { lives, drafts ->
                Pair(lives, drafts)
            })
    }

    private fun requestAllPosts(
        blog: Blogs,
        accessToken: String
    ): Observable<Pair<List<Posts>, List<Posts>>> {
        val live = postsRepository.requestLivePosts(accessToken, blog.id!!)
        val draft = postsRepository.requestDraftPosts(accessToken, blog.id!!)
        return Observable.combineLatest(
            live.toObservable(),
            draft.toObservable(),
            BiFunction { lives, drafts ->
                postsRepository.savePosts(lives, false)
                postsRepository.savePosts(drafts, true)
                if (lives.isNotEmpty() || drafts.isNotEmpty()) {
                    val now = timeRepository.getCurrentTime()
                    blogRepository.updateLastPostListRequest(blog, now)
                }
                Pair(lives, drafts)
            })
    }
}
