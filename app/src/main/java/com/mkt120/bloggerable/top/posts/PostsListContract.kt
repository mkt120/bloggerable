package com.mkt120.bloggerable.top.posts

import com.mkt120.bloggerable.api.PostsResponse
import com.mkt120.bloggerable.model.Posts

interface PostsListContract {
    interface PostsListView {
        fun setPostsResponse(response: PostsResponse?)
        fun showPostsItem(posts: Posts, type: Int)
    }

    interface PostsListPresenter {
        fun onActivityCreated(response: PostsResponse?)
        fun onClickPosts(posts: Posts)
    }

}