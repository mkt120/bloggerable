package com.mkt120.bloggerable.usecase

import android.text.Html
import android.text.Spanned
import com.mkt120.bloggerable.ApiManager
import com.mkt120.bloggerable.datasource.BloggerApiDataSource
import com.mkt120.bloggerable.model.posts.Posts
import com.mkt120.bloggerable.repository.AccountRepository
import io.realm.RealmList

class UpdatePosts(
    private val getAccessToken: GetAccessToken,
    private val bloggerApiDataSource: BloggerApiDataSource
) {

    fun execute(
        userId:String,
        posts: Posts,
        title: String,
        content: Spanned,
        labels: Array<String>?,
        listener: ApiManager.CompleteListener
    ) {
        val accessToken = getAccessToken.execute(userId, object : AccountRepository.OnRefreshListener {
            override fun onRefresh() {
                execute(userId, posts, title, content, labels, listener)
            }
            override fun onErrorResponse(code: Int, message: String) {
                listener.onErrorResponse(code, message)
            }
            override fun onFailed(t: Throwable) {
                listener.onFailed(t)
            }
        })
        accessToken?.let {
            updatePosts(accessToken, posts, title, content, labels, listener)
        }
    }

    private fun updatePosts(
        accessToken: String,
        posts: Posts,
        title: String,
        content: Spanned,
        labels: Array<String>?,
        completeListener: ApiManager.CompleteListener
    ) {
        val html = Html.toHtml(content, Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL)
        posts.apply {
            this.title = title
            this.content = html
            this.labels = RealmList<String>()
            this.labels!!.addAll(labels!!)
        }
        bloggerApiDataSource.updatePosts(accessToken, posts, completeListener)
    }
}
