package com.mkt120.bloggerable.top

import com.mkt120.bloggerable.ApiManager
import com.mkt120.bloggerable.R
import com.mkt120.bloggerable.api.PostsResponse
import com.mkt120.bloggerable.create.CreatePostsActivity
import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.model.posts.Posts
import com.mkt120.bloggerable.top.posts.PostsListFragment
import com.mkt120.bloggerable.usecase.*
import com.mkt120.bloggerable.util.PreferenceManager

class TopPresenter(
    private val view: TopContract.TopView,
    private val getLastSelectBlogId: GetLastSelectBlogId,
    private val saveLastSelectBlogId: SaveLastSelectBlogId,
    private val findAllBlogs: FindAllBlogs,
    private val getAllPosts: RequestAllPosts,
    private val saveAllPosts: SaveAllPosts
) :
    TopContract.TopPresenter {

    private lateinit var currentBlog: Blogs

    override fun initialize() {
        val blogs = findAllBlogs.execute()
        if (blogs.isEmpty()) {
            // todo:空
            return
        }

        val blogId = getLastSelectBlogId.execute()
        currentBlog = blogs.find { it.id == blogId } ?: blogs[0]
        view.setTitle(currentBlog.name!!)
        view.onBindDrawer(blogs)
        requestPosts(currentBlog)
    }

    override fun onClickFab() {
        val blogId = currentBlog.id!!
        view.showCreateScreen(blogId)
    }

    override fun onClickBlog(blogs: Blogs) {
        currentBlog = blogs
        saveLastSelectBlogId.execute(currentBlog.id!!)
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
        val blogId = blogs.id!!
        getAllPosts.execute(false, blogId, object : ApiManager.PostsListener {
            override fun onResponse(posts: PostsResponse?) {
                posts?.let {
                    if (it.items != null) {
                        saveAllPosts.execute(it.items!!.toList(), false)
                        PreferenceManager.labelList = posts.createLabelList()
                    }
                    view.notifyDataSetChanged()
                }
                // todo: 待ち合わせ
                view.dismissProgress()
            }
        })
        getAllPosts.execute(true, blogId, object : ApiManager.PostsListener {
            override fun onResponse(posts: PostsResponse?) {
                posts?.let {
                    if (it.items != null) {
                        saveAllPosts.execute(it.items!!.toList(), true)
                    }
                    view.notifyDataSetChanged()
                }
                // todo: 待ち合わせ
                view.dismissProgress()
            }
        })
    }
}