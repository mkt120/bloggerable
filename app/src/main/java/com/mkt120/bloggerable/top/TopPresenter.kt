package com.mkt120.bloggerable.top

import com.mkt120.bloggerable.ApiManager
import com.mkt120.bloggerable.PreferenceManager
import com.mkt120.bloggerable.R
import com.mkt120.bloggerable.api.BlogsResponse
import com.mkt120.bloggerable.api.PostsResponse
import com.mkt120.bloggerable.create.CreatePostsActivity
import com.mkt120.bloggerable.model.Blogs
import com.mkt120.bloggerable.model.Posts
import com.mkt120.bloggerable.top.posts.PostsListFragment

class TopPresenter(private val view: TopContract.TopView) :
    TopContract.TopPresenter {

    private lateinit var currentBlog: Blogs

    override fun onCreate(response: BlogsResponse) {
        currentBlog = response.items!![0]
        view.setTitle(currentBlog.name!!)
        view.onBindDrawer(response)
        requestPosts(currentBlog)
    }

    override fun onClickFab() {
        val blogId = currentBlog.id!!
        view.showCreateScreen(blogId)
    }

    override fun onClickBlog(blogs: Blogs) {
        currentBlog = blogs
        if (view.isDrawerOpen()) {
            view.closeDrawer()
        }
        view.setTitle(currentBlog.name!!)
        requestPosts(currentBlog)
    }

    override fun onClickDrawerItem(itemsResId: Int) {
        view.closeDrawer()
        view.showAboutAppScreen()
    }

    override fun onBackPressed(): Boolean {
        if (view.isDrawerOpen()) {
            view.closeDrawer()
            return true
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int) {
        requestPosts(currentBlog)

        if (resultCode == CreatePostsActivity.RESULT_POSTS_UPDATE) {
            view.setPagerPosition(0)
        }
        if (resultCode == CreatePostsActivity.RESULT_DRAFT_UPDATE) {
            view.setPagerPosition(1)
        }
    }

    override fun onMenuItemClick(itemId: Int?): Boolean {
        itemId?.let {
            return when (it) {
                R.id.open_in_browser -> {
                    view.openBrowser(currentBlog.url!!)
                    true
                }
                R.id.about_this_blog -> {
                    view.showAboutDialog(currentBlog)
                    true
                }
                else -> {
                    false
                }
            }
        }
        return false
    }

    override fun onClickPosts(posts: Posts, listType: Int) {
        if (listType == PostsListFragment.LIST_POSTS) {
            // publish
            view.showEditScreen(posts, false)
        } else {
            view.showEditScreen(posts, true)
        }
    }

    private fun requestPosts(blogs: Blogs) {
        view.showProgress()
        val blogId = blogs.id
        ApiManager.getPosts(
            blogId!!,
            object : ApiManager.PostsListener {
                override fun onResponse(posts: PostsResponse?) {
                    posts?.let {
                        PreferenceManager.labelList = posts.createLabelList()
                        view.updateLivePosts(posts)
                    }
                    // todo: 待ち合わせ
                    view.dismissProgress()
                }
            })
        ApiManager.getDraftPosts(
            blogId,
            object : ApiManager.PostsListener {
                override fun onResponse(posts: PostsResponse?) {
                    posts?.let {
                        view.updateDraftPost(posts)
                    }
                    // todo: 待ち合わせ
                    view.dismissProgress()
                }
            })
    }
}