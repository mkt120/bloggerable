package com.mkt120.bloggerable.top.posts.item

import android.text.Html
import com.mkt120.bloggerable.model.posts.Posts


class PostsItemPresenter(val view: PostsItemContract.PostsItemView) :
    PostsItemContract.PostsItemPresenter {
    override fun onBindData(posts: Posts) {
        //todo:改善の余地あり

        val title = Html.fromHtml(posts.title, Html.FROM_HTML_MODE_LEGACY).toString()
        view.setTitle(title)

        val content =
            Html.fromHtml(posts.content, Html.FROM_HTML_MODE_LEGACY).toString().trim()
                .replace("\n", " ")
        view.setContent(content)

        val commendCount = posts.replies!!.totalItems.toString()
        view.setCommentCount(commendCount)

        val date = posts.getStringDate()
        view.setPublishDate(date)
    }
}
