package com.mkt120.bloggerable.top

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.mkt120.bloggerable.BaseActivity
import com.mkt120.bloggerable.R
import com.mkt120.bloggerable.about.AboutAppActivity
import com.mkt120.bloggerable.create.CreatePostsActivity
import com.mkt120.bloggerable.datasource.BloggerApiDataSource
import com.mkt120.bloggerable.datasource.PreferenceDataSource
import com.mkt120.bloggerable.datasource.RealmDataSource
import com.mkt120.bloggerable.login.LoginActivity
import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.model.posts.Posts
import com.mkt120.bloggerable.repository.AccountRepository
import com.mkt120.bloggerable.repository.BlogRepository
import com.mkt120.bloggerable.repository.CurrentBlogIdRepository
import com.mkt120.bloggerable.repository.PostsRepository
import com.mkt120.bloggerable.top.drawer.BlogListAdapter
import com.mkt120.bloggerable.usecase.*
import com.mkt120.bloggerable.util.RealmManager
import kotlinx.android.synthetic.main.activity_top.*

class TopActivity : BaseActivity(), Toolbar.OnMenuItemClickListener, TopContract.TopView {
    companion object {
        private const val EXTRA_KEY_BLOG_ID = "EXTRA_KEY_BLOG_ID"

        fun createIntent(context: Context, blogId: String): Intent =
            Intent(context, TopActivity::class.java).apply {
                putExtra(EXTRA_KEY_BLOG_ID, blogId)
            }
    }

    private lateinit var adapter: PostsPagerAdapter
    private lateinit var presenter: TopPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top)

        val blogId = intent.getStringExtra(EXTRA_KEY_BLOG_ID)
        adapter = PostsPagerAdapter(
            applicationContext,
            blogId,
            supportFragmentManager
        )

        view_pager.adapter = adapter
        tabs.setupWithViewPager(view_pager)
        fab.setOnClickListener {
            presenter.onClickFab()
        }

        val realmDataSource = RealmDataSource(RealmManager(getRealm()))
        val preferenceDataSource = PreferenceDataSource()
        val currentBlogIdRepository = CurrentBlogIdRepository(preferenceDataSource)

        val bloggerApiDataSource = BloggerApiDataSource()
        val blogsRepository =
            BlogRepository(bloggerApiDataSource, realmDataSource)

        val saveLastSelectBlogId = SaveCurrentBlogId(currentBlogIdRepository)

        val postsRepository = PostsRepository(bloggerApiDataSource, realmDataSource)
        val findAllBlogs = FindAllBlog(blogsRepository)
        val accountRepository = AccountRepository(bloggerApiDataSource, preferenceDataSource)
        val getCurrentAccount = GetCurrentAccount(accountRepository)
        val getAccessToken = GetAccessToken(accountRepository)
        val getAllPosts = GetAllPosts(getAccessToken, postsRepository, blogsRepository)
        val getLabels = GetLabels(blogsRepository)

        presenter = TopPresenter(
            this@TopActivity,
            getCurrentAccount,
            saveLastSelectBlogId,
            findAllBlogs,
            getAllPosts,
            getLabels
        )
        presenter.initialize()
    }

    override fun setItemMenu(itemMenu: Int) {
        tool_bar.inflateMenu(R.menu.posts_list_menu)
        tool_bar.setOnMenuItemClickListener(this)
    }

    override fun setTitle(title: String) {
        tool_bar.title = title
    }

    override fun initDrawerLayout() {
        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            tool_bar,
            R.string.app_name,
            R.string.app_name
        )
        drawer_layout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
    }

    override fun notifyDataSetChanged() {
        adapter.notifyDataSetChanged()
    }

    override fun updateCurrentBlog(blogId: String) {
        adapter.updateCurrentBlog(blogId)
    }

    override fun showAboutAppScreen() {
        val intent = Intent(this@TopActivity, AboutAppActivity::class.java)
        startActivity(intent)
    }

    override fun onBindDrawer(response: List<Blogs>) {
        drawer_view.onBindData(response, object : BlogListAdapter.MenuClickListener {
            override fun onClick(itemResId: Int) {
                presenter.onClickDrawerItem(itemResId)
            }

            override fun onClick(blogs: Blogs) {
                presenter.onClickBlog(blogs)
            }
        })
    }

    override fun setPagerPosition(position: Int) {
        view_pager.currentItem = position
    }

    override fun onBackPressed() {
        if (!presenter.onBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun closeDrawer() {
        drawer_layout.closeDrawer(drawer_view)
    }

    override fun isDrawerOpen(): Boolean {
        return drawer_layout.isDrawerOpen(drawer_view)
    }

    override fun showCreateScreen(blogId: String, labels: ArrayList<String>) {
        val intent = CreatePostsActivity.createIntent(
            this@TopActivity,
            blogId,
            labels
        )
        startActivityForResult(
            intent,
            CreatePostsActivity.REQUEST_CREATE_POSTS
        )
    }

    override fun showEditScreen(posts: Posts, labels: ArrayList<String>, isDraft: Boolean) {
        val i = CreatePostsActivity.createPostsIntent(
            this@TopActivity,
            posts,
            labels,
            isDraft
        )
        val requestCode = if (isDraft) {
            CreatePostsActivity.REQUEST_EDIT_POSTS
        } else {
            CreatePostsActivity.REQUEST_EDIT_DRAFT
        }
        startActivityForResult(
            i,
            requestCode
        )
    }

    override fun openBrowser(url: String) {
        val builder = CustomTabsIntent.Builder()
            .setToolbarColor(ContextCompat.getColor(this@TopActivity, R.color.colorPrimary))
        builder.build().launchUrl(this@TopActivity, Uri.parse(url))
    }

    override fun showAboutDialog(blogs: Blogs) {
//        val dialogFragment =
//            BlogInfoDialogFragment.newInstance(
//                blogs
//            )
//        dialogFragment.show(supportFragmentManager, null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            return
        }
        presenter.onActivityResult(requestCode, resultCode)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return presenter.onMenuItemClick(item?.itemId)
    }

    override fun showEmptyBlogScreen() {
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        fab.visibility = View.GONE
        tabs.visibility = View.GONE
        empty_blog_view.visibility = View.VISIBLE
        create_blog_button.setOnClickListener {
            presenter.onClickCreateBlogButton()
        }
        refresh_button.setOnClickListener {
            presenter.onClickRefreshButton()
        }
    }

    override fun showLoginScreen() {
        val intent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun showError(code: Int, message: String?) {
        message?.let {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }

    override fun showProgress() {
        progress_view.visibility = View.VISIBLE
    }

    override fun dismissProgress() {
        progress_view.visibility = View.GONE
    }

    fun onClickPostsItem(posts: Posts, type: TopContract.TYPE) {
        presenter.onClickPosts(posts, type)
    }
}