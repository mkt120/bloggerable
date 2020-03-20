package com.mkt120.bloggerable.top.posts

import com.mkt120.bloggerable.util.RealmManager
import com.mkt120.bloggerable.model.posts.Posts

class PostsListPresenter(
    realmManager: RealmManager,
    private val view: PostsListContract.PostsListView,
    blogId: String,
    private val listType: Int
) : PostsListContract.PostsListPresenter {

    private var postsList : List<Posts>?

    init {
        val isPost = listType == PostsListFragment.LIST_POSTS
        postsList = realmManager.findAllPosts(blogId, isPost)
    }

    override fun onClickPosts(posts: Posts) {
        view.showPostsItem(posts, listType)
    }

    override fun onActivityCreated() {
        view.setPostsResponse(postsList!!)
    }
}
