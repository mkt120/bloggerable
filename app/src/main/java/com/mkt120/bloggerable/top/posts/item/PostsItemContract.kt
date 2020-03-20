package com.mkt120.bloggerable.top.posts.item

import com.mkt120.bloggerable.model.posts.Posts

class PostsItemContract {
    interface PostsItemView {
        fun setTitle(title: String)
        fun setContent(content: String)
        fun setCommentCount(count: String)
        fun setPublishDate(date: String)
    }

    interface PostsItemPresenter {
        fun onBindData(posts: Posts)
    }
}