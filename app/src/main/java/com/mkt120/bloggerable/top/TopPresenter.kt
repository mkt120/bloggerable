package com.mkt120.bloggerable.top

import com.mkt120.bloggerable.ApiManager
import com.mkt120.bloggerable.R
import com.mkt120.bloggerable.api.PostsResponse
import com.mkt120.bloggerable.create.CreatePostsActivity
import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.model.posts.Posts
import com.mkt120.bloggerable.top.posts.PostsListFragment
import com.mkt120.bloggerable.util.PreferenceManager
import com.mkt120.bloggerable.util.RealmManager

class TopPresenter(private val realmManager: RealmManager, private val view: TopContract.TopView) :
    TopContract.TopPresenter {

    private lateinit var currentBlog: Blogs

    override fun onCreate() {
        val blogs = realmManager.findAllBlogs()
        if (blogs != null) {
            // todo: blogsが空
            val blogId = PreferenceManager.lastSelectBlogId
            if (blogId.isEmpty()) {
                currentBlog = blogs[0]
                PreferenceManager.lastSelectBlogId = currentBlog.id ?: ""
            } else {
                for (blog in blogs) {
                    if (blogId == blog.id) {
                        currentBlog = blog
                        break
                    }
                }
            }
            view.setTitle(currentBlog.name!!)
            view.onBindDrawer(blogs)
            val posts = realmManager.findAllPosts(currentBlog.id!!, true)
            if (posts == null) {
                requestPosts(currentBlog)
            }
        }
    }

    override fun onClickFab() {
        val blogId = currentBlog.id!!
        view.showCreateScreen(blogId)
    }

    override fun onClickBlog(blogs: Blogs) {
        currentBlog = blogs
        PreferenceManager.lastSelectBlogId = currentBlog.id!!
        if (view.isDrawerOpen()) {
            view.closeDrawer()
        }
        view.setTitle(currentBlog.name!!)
        val posts = realmManager.findAllPosts(currentBlog.id!!, true)
        if (posts == null) {
            requestPosts(currentBlog)
        } else {
            view.updateCurrentBlog(currentBlog.id!!)
        }
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
                        if (it.items != null) {
                            realmManager.addAllPosts(it.items!!.toList(), true)
                            PreferenceManager.labelList = posts.createLabelList()
                        }
                        view.notifyDataSetChanged()
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
                        if (it.items != null) {
                            realmManager.addAllPosts(it.items!!.toList(), false)
                        }
                        view.notifyDataSetChanged()
                    }
                    // todo: 待ち合わせ
                    view.dismissProgress()
                }
            })
    }
}