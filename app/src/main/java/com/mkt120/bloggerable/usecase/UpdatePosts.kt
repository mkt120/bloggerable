package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.model.posts.Posts
import com.mkt120.bloggerable.repository.Repository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.RealmList

class UpdatePosts(
    private val getAccessToken: UseCase.IGetAccessToken,
    private val postsRepository: Repository.IPostsRepository
) {

    fun execute(
        userId: String,
        posts: Posts,
        title: String,
        html: String,
        labels: Array<String>?,
        onComplete: () -> Unit,
        onFailed: (Throwable) -> Unit
    ) {
        getAccessToken.execute(userId, System.currentTimeMillis())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .flatMapCompletable { accessToken ->
                posts.apply {
                    this.title = title
                    this.content = html
                    this.labels = RealmList<String>()
                    this.labels!!.addAll(labels!!)
                }
                postsRepository.updatePosts(accessToken, posts)
            }.subscribe(onComplete, onFailed)
    }
}
