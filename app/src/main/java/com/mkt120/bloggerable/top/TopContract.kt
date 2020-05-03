package com.mkt120.bloggerable.top

import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.model.posts.Posts

interface TopContract {

    interface TopView {
        fun setTitle(title: String)
        fun showLoginScreen()
        fun showCreateScreen(blogId: String, labels: ArrayList<String>)
        fun showEditScreen(posts: Posts, labels: ArrayList<String>, isDraft: Boolean)
        fun showAboutAppScreen()
        fun setPagerPosition(position: Int)
        fun openBrowser(url: String)
        fun showAboutDialog(blogs: Blogs)
        fun notifyDataSetChanged()
        fun updateCurrentBlog(blogId: String)
        fun closeDrawer()
        fun isDrawerOpen(): Boolean
        fun onBindDrawer(response: List<Blogs>)
        fun showProgress()
        fun dismissProgress()
        fun showError(code: Int, message: String?)
    }

    interface TopPresenter {
        fun initialize()
        fun onClickFab()
        fun onClickBlog(blogs: Blogs)
        fun onClickDrawerItem(itemsResId: Int)
        fun onBackPressed(): Boolean
        fun onActivityResult(requestCode: Int, resultCode: Int)
        fun onMenuItemClick(itemId: Int?): Boolean
        fun onClickPosts(posts: Posts, listType: Int)
    }

}