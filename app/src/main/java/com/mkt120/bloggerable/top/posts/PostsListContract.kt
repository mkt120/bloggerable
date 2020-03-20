package com.mkt120.bloggerable.top.posts

import com.mkt120.bloggerable.model.posts.Posts

interface PostsListContract {
    interface PostsListView {
        fun setPostsResponse(response: List<Posts>)
        fun showPostsItem(posts: Posts, type: Int)
    }

    interface PostsListPresenter {
        fun onActivityCreated()
        fun onClickPosts(posts: Posts)
    }

}