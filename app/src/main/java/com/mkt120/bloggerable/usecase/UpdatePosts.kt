package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.model.posts.Posts
import com.mkt120.bloggerable.repository.Repository
import io.reactivex.Completable
import io.realm.RealmList

class UpdatePosts(
    private val getAccessToken: UseCase.IGetAccessToken,
    private val postsRepository: Repository.IPostsRepository
) {

    fun execute(
        now: Long,
        userId: String,
        posts: Posts,
        title: String,
        html: String,
        labels: Array<String>?
    ): Completable = getAccessToken.execute(userId, now)
        .flatMapCompletable { accessToken ->
            posts.apply {
                this.title = title
                this.content = html
                this.labels = RealmList<String>()
                if (labels != null) {
                    this.labels!!.addAll(labels)
                }
            }
            postsRepository.updatePosts(accessToken, posts)
        }
}
