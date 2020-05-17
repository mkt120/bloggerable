package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.datasource.BloggerApiDataSource
import com.mkt120.bloggerable.model.posts.Posts
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.RealmList

class UpdatePosts(
    private val getAccessToken: GetAccessToken,
    private val bloggerApiDataSource: BloggerApiDataSource
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
        getAccessToken.execute(userId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .flatMapCompletable { accessToken ->
                posts.apply {
                    this.title = title
                    this.content = html
                    this.labels = RealmList<String>()
                    this.labels!!.addAll(labels!!)
                }
                bloggerApiDataSource.updatePosts(accessToken, posts)
            }.subscribe(onComplete, onFailed)
    }
}
