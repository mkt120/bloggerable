package com.mkt120.bloggerable.top

import com.mkt120.bloggerable.api.BlogsResponse
import com.mkt120.bloggerable.api.PostsResponse
import com.mkt120.bloggerable.model.Blogs
import com.mkt120.bloggerable.model.Posts

interface TopContract {

    interface TopView {
        fun setTitle(title: String)
        fun showCreateScreen(blogId: String)
        fun showEditScreen(posts: Posts, isDraft: Boolean)
        fun showAboutAppScreen()
        fun setPagerPosition(position: Int)
        fun openBrowser(url: String)
        fun showAboutDialog(blogs: Blogs)
        fun updateDraftPost(posts: PostsResponse)
        fun updateLivePosts(posts: PostsResponse)
        fun closeDrawer()
        fun isDrawerOpen(): Boolean
        fun onBindDrawer(response: BlogsResponse)
    }

    interface TopPresenter {
        fun onCreate(response: BlogsResponse)
        fun onClickFab()
        fun onClickBlog(blogs: Blogs)
        fun onClickDrawerItem(itemsResId: Int)
        fun onBackPressed(): Boolean
        fun onActivityResult(requestCode: Int, resultCode: Int)
        fun onMenuItemClick(itemId: Int?): Boolean
        fun onClickPosts(posts: Posts, listType: Int)
    }

}