package com.mkt120.bloggerable.top.posts

import com.mkt120.bloggerable.model.posts.Posts
import com.mkt120.bloggerable.top.TopContract
import com.mkt120.bloggerable.usecase.FindAllPosts

class PostsListPresenter(
    findAllPosts: FindAllPosts,
    private val view: PostsListContract.PostsListView,
    blogId: String?,
    private val type: TopContract.TYPE
) : PostsListContract.PostsListPresenter {

    private val postsList: List<Posts> =
        findAllPosts.execute(blogId, type == TopContract.TYPE.POST).blockingGet()

    override fun onClickPosts(posts: Posts) {
        view.showPostsItem(type, posts)
    }

    override fun onActivityCreated() {
        view.setPostsResponse(type, postsList)
    }
}
