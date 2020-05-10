package com.mkt120.bloggerable.top.posts

import com.mkt120.bloggerable.model.posts.Posts
import com.mkt120.bloggerable.top.TopContract

interface PostsListContract {

    interface PostsListView {
        fun setPostsResponse(type: TopContract.TYPE, response: List<Posts>)
        fun showPostsItem(type: TopContract.TYPE, posts: Posts)
    }

    interface PostsListPresenter {
        fun onActivityCreated()
        fun onClickPosts(posts: Posts)
    }

}