package com.mkt120.bloggerable.top.posts

import com.mkt120.bloggerable.api.PostsResponse
import com.mkt120.bloggerable.model.Posts

class PostsListPresenter(
    private val view: PostsListContract.PostsListView,
    private val listType: Int
) : PostsListContract.PostsListPresenter {
    override fun onClickPosts(posts: Posts) {
        view.showPostsItem(posts, listType)
    }

    override fun onActivityCreated(response: PostsResponse?) {
        view.setPostsResponse(response)
    }
}
