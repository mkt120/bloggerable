package com.mkt120.bloggerable.top

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.mkt120.bloggerable.about.AboutAppActivity
import com.mkt120.bloggerable.create.CreatePostsActivity
import com.mkt120.bloggerable.R
import com.mkt120.bloggerable.api.BlogsResponse
import com.mkt120.bloggerable.api.PostsResponse
import com.mkt120.bloggerable.model.Blogs
import com.mkt120.bloggerable.model.Posts
import com.mkt120.bloggerable.top.drawer.DrawerView
import com.mkt120.bloggerable.top.infodialog.BlogInfoDialogFragment
import kotlinx.android.synthetic.main.activity_top.*

class TopActivity : AppCompatActivity(), Toolbar.OnMenuItemClickListener, TopContract.TopView {

    companion object {
        private const val EXTRA_KEY_BLOG_LIST = "EXTRA_KEY_BLOG_LIST"

        fun createIntent(context: Context, blogsResponse: BlogsResponse): Intent =
            Intent(context, TopActivity::class.java).apply {
                putExtra(EXTRA_KEY_BLOG_LIST, blogsResponse)
            }
    }

    private lateinit var adapter: PostsPagerAdapter
    private lateinit var presenter: TopPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top)
        tool_bar.inflateMenu(R.menu.posts_list_menu)
        tool_bar.setOnMenuItemClickListener(this)
        adapter = PostsPagerAdapter(
            applicationContext,
            null,
            null,
            supportFragmentManager
        )

        view_pager.adapter = adapter
        tabs.setupWithViewPager(view_pager)
        fab.setOnClickListener {
            presenter.onClickFab()
        }

        val response = intent.getParcelableExtra<BlogsResponse>(EXTRA_KEY_BLOG_LIST)!!
        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            tool_bar,
            R.string.app_name,
            R.string.app_name
        )
        drawer_layout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        presenter = TopPresenter(this@TopActivity)
        presenter.onCreate(response)
    }

    override fun setTitle(title: String) {
        tool_bar.title = title
    }

    override fun updateDraftPost(posts: PostsResponse) {
        adapter.updateDraftPosts(posts)
    }

    override fun updateLivePosts(posts: PostsResponse) {
        adapter.updateListPosts(posts)
    }

    override fun showAboutAppScreen() {
        val intent = Intent(this@TopActivity, AboutAppActivity::class.java)
        startActivity(intent)
    }

    override fun onBindDrawer(response: BlogsResponse) {
        drawer_view.onBindData(response, object : DrawerView.BlogListAdapter.MenuClickListener {
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

    override fun showCreateScreen(blogId: String) {
        val intent = CreatePostsActivity.createIntent(
            this@TopActivity,
            blogId
        )
        startActivityForResult(
            intent,
            CreatePostsActivity.REQUEST_CREATE_POSTS
        )
    }

    override fun showEditScreen(posts: Posts, isDraft: Boolean) {
        val i = CreatePostsActivity.createPostsIntent(
            this@TopActivity,
            posts,
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
        val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(i)
    }

    override fun showAboutDialog(blogs: Blogs) {
        val dialogFragment =
            BlogInfoDialogFragment.newInstance(
                blogs
            )
        dialogFragment.show(supportFragmentManager, null)
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

    fun onClickPostsItem(posts: Posts, listType: Int) {
        presenter.onClickPosts(posts, listType)
    }
}