package com.mkt120.bloggerable.top

import com.mkt120.bloggerable.ApiManager
import com.mkt120.bloggerable.R
import com.mkt120.bloggerable.api.PostResponse
import com.mkt120.bloggerable.create.CreatePostsActivity
import com.mkt120.bloggerable.model.Account
import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.model.posts.Posts
import com.mkt120.bloggerable.top.posts.PostsListFragment
import com.mkt120.bloggerable.usecase.*

class TopPresenter(
    private val view: TopContract.TopView,
    private val getCurrentAccount: GetCurrentAccount,
    private val saveLastSelectBlogId: SaveCurrentBlogId,
    private val findAllBlogs: FindAllBlog,
    private val getAllPosts: RequestAllPosts,
    private val saveAllPosts: SaveAllPosts,
    private val getLabels: GetLabels
) :
    TopContract.TopPresenter {

    private var currentAccount : Account? = getCurrentAccount.execute()
    private lateinit var currentBlog: Blogs

    companion object {
        private const val CODE_ERROR_ON_FAILED = -99
    }

    override fun initialize() {
        if (currentAccount == null) {
            view.showLoginScreen()
            return
        }

        val blogs = findAllBlogs.execute(currentAccount!!.getId())
        view.onBindDrawer(blogs)

        if (blogs.isEmpty()) {
            // todo: EmptyView
            return
        }

        var blogId = currentAccount!!.getCurrentBlogId()
        if (blogId.isEmpty()) {
            blogId = blogs[0].id!!
        }

        val current = blogs.find { it.id == blogId } ?: blogs[0]
        bindCurrentBlog(current)
        val isExpired = current.isExpired(System.currentTimeMillis())
        if (isExpired) {
            requestPosts(currentAccount!!.getId(), current)
            return
        }
    }

    override fun onClickFab() {
        // 新規作成ボタン
        val blogId = currentBlog.id!!
        val labels = getLabels.execute(blogId)
        view.showCreateScreen(blogId, labels)
    }

    override fun onClickBlog(blogs: Blogs) {
        // サイドメニューのブログタップ
        if (view.isDrawerOpen()) {
            view.closeDrawer()
        }
        bindCurrentBlog(blogs)
        val isExpired = blogs.isExpired(System.currentTimeMillis())
        if (isExpired) {
            requestPosts(currentAccount!!.getId(), blogs)
            return
        }

    }

    private fun bindCurrentBlog(blog: Blogs) {
        // 現在のブログを設定
        currentBlog = blog
        currentAccount!!.setCurrentBlogId(blog.id!!)
        saveLastSelectBlogId.execute(currentAccount!!)

        view.setTitle(currentBlog.name!!)
        view.updateCurrentBlog(currentBlog.id!!)
    }

    override fun onClickDrawerItem(itemsResId: Int) {
        // サイドメニューの項目タップ
        view.closeDrawer()
        view.showAboutAppScreen()
    }

    override fun onBackPressed(): Boolean {
        // バックキー
        if (view.isDrawerOpen()) {
            view.closeDrawer()
            return true
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int) {
        requestPosts(currentAccount!!.getId(), currentBlog)

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
        val labels = getLabels.execute(posts.blog!!.id!!)
        if (listType == PostsListFragment.LIST_POSTS) {
            // publish
            view.showEditScreen(posts, labels, false)
        } else {
            view.showEditScreen(posts, labels, true)
        }
    }

    private fun requestPosts(userId: String, blog: Blogs) {
        // 記事一覧取得
        view.showProgress()
        getAllPosts.execute(System.currentTimeMillis(), userId, false, blog, object : ApiManager.PostsListener {
            override fun onResponse(reponse: PostResponse?) {
                reponse?.let {
                    if (it.items != null) {
                        saveAllPosts.execute(it.items!!.toList(), false)
                    }
                    view.notifyDataSetChanged()
                }
                view.dismissProgress()
            }

            override fun onErrorResponse(code: Int, message: String) {
                view.showError(code, message)
            }

            override fun onFailed(t: Throwable) {
                view.showError(CODE_ERROR_ON_FAILED, t.message)
            }

        })
        getAllPosts.execute(System.currentTimeMillis(), userId, true, blog, object : ApiManager.PostsListener {
            override fun onResponse(reponse: PostResponse?) {
                reponse?.let {
                    if (it.items != null) {
                        saveAllPosts.execute(it.items!!.toList(), true)
                    }
                    view.notifyDataSetChanged()
                }
                view.dismissProgress()
            }

            override fun onErrorResponse(code: Int, message: String) {
                view.showError(code, message)
            }

            override fun onFailed(t: Throwable) {
                view.showError(CODE_ERROR_ON_FAILED, t.message)
            }
        })
    }
}