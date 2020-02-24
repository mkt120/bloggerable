package com.mkt120.bloggerable

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.mkt120.bloggerable.api.BlogsResponse
import com.mkt120.bloggerable.api.PostsResponse
import com.mkt120.bloggerable.model.Blogs
import com.mkt120.bloggerable.model.Posts
import kotlinx.android.synthetic.main.activity_posts_list.*
import kotlinx.android.synthetic.main.fragment_posts_list.*
import kotlinx.android.synthetic.main.include_posts_view_holder.view.*

class PostsListActivity : AppCompatActivity(), Toolbar.OnMenuItemClickListener {

    companion object {
        private const val EXTRA_KEY_BLOG_LIST = "EXTRA_KEY_BLOG_LIST"

        fun createIntent(context: Context, blogsResponse: BlogsResponse): Intent =
            Intent(context, PostsListActivity::class.java).apply {
                putExtra(EXTRA_KEY_BLOG_LIST, blogsResponse)
            }
    }

    private var adapter: Adapter? = null
    private var currentBlog : Blogs? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts_list)
        val response = intent.getParcelableExtra<BlogsResponse>(EXTRA_KEY_BLOG_LIST)
        currentBlog = response!!.items!![0]
        tool_bar.title = currentBlog!!.name
        tool_bar.inflateMenu(R.menu.posts_list_menu)
        tool_bar.setOnMenuItemClickListener(this)
        adapter = Adapter(applicationContext, null, null, supportFragmentManager)
        view_pager.adapter = adapter
        tabs.setupWithViewPager(view_pager)
        fab.setOnClickListener {
            val blogId = currentBlog!!.id
            val intent = CreatePostsActivity.createIntent(this@PostsListActivity, blogId!!)
            startActivityForResult(intent, CreatePostsActivity.REQUEST_CREATE_POSTS)
        }

        requestPosts(currentBlog!!)
        drawer_view.bindData(response, object : DrawerView.BlogListAdapter.MenuClickListener {
            override fun onClick(itemResId: Int) {
                Toast.makeText(this@PostsListActivity, itemResId, Toast.LENGTH_SHORT).show()
            }

            override fun onClick(blogs: Blogs) {
                drawer_layout.closeDrawer(drawer_view)
                currentBlog = blogs
                tool_bar.title = currentBlog!!.name
                requestPosts(currentBlog!!)
            }
        })

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

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(drawer_view)) {
            drawer_layout.closeDrawer(drawer_view)
            return
        }
        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            return
        }

        // 更新
        requestPosts(currentBlog!!)

        if (resultCode == CreatePostsActivity.RESULT_POSTS_UPDATE) {
            view_pager.currentItem = 0
        }
        if (resultCode == CreatePostsActivity.RESULT_DRAFT_UPDATE) {
            view_pager.currentItem = 1
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        item?.let {
            return when (item.itemId) {
                R.id.open_in_browser -> {
                    val url = currentBlog!!.url
                    val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(i)
                    true
                }
                else -> {
                    false
                }
            }
        }
        return false
    }

    private fun requestPosts(blogs : Blogs) {
        val blogId = blogs.id
        ApiManager.getPosts(blogId!!, object : ApiManager.PostsListener {
            override fun onResponse(posts: PostsResponse?) {
                posts?.let {
                    PreferenceManager.labelList = posts.createLabelList()
                    adapter!!.updateListPosts(posts)
                }
            }
        })
        ApiManager.getDraftPosts(blogId, object : ApiManager.PostsListener {
            override fun onResponse(posts: PostsResponse?) {
                posts?.let {
                    adapter!!.updateDraftPosts(posts)
                }
            }
        })
    }

    fun onClickPostsItem(posts: Posts, listType: Int) {
        if (listType == PostsListFragment.LIST_POSTS) {
            // publish
            val i = CreatePostsActivity.createPostsIntent(this@PostsListActivity, posts)
            startActivityForResult(i, CreatePostsActivity.REQUEST_EDIT_POSTS)
        } else {
            // draft
            val i = CreatePostsActivity.createDraftIntent(this@PostsListActivity, posts)
            startActivityForResult(i, CreatePostsActivity.REQUEST_EDIT_DRAFT)
        }
    }

    /**
     * 記事一覧(投稿・下書き)画面をそれぞれ表示するPagerAdapter
     */
    class Adapter(
        private var context: Context,
        private var livePosts: PostsResponse?,
        private var draftPosts: PostsResponse?,
        fragmentManager: FragmentManager
    ) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        companion object {
            private val PAGE_TITLES =
                arrayOf(R.string.posts_list_title_posts, R.string.posts_list_title_draft)
        }

        override fun getItem(position: Int): Fragment {
            Log.d("Adapter", "getItem position=$position")
            return if (position == 0) {
                PostsListFragment.newInstance(livePosts, PostsListFragment.LIST_POSTS)
            } else {
                PostsListFragment.newInstance(draftPosts, PostsListFragment.LIST_DRAFT)
            }
        }

        fun updateListPosts(posts: PostsResponse) {
            livePosts = posts
            notifyDataSetChanged()
        }

        override fun getItemPosition(`object`: Any): Int {
            return PagerAdapter.POSITION_NONE
        }

        fun updateDraftPosts(posts: PostsResponse) {
            draftPosts = posts
            notifyDataSetChanged()
        }

        override fun getCount(): Int {
            return PAGE_TITLES.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return context.getString(PAGE_TITLES[position])
        }
    }

    /**
     * 記事一覧を表示するFragment
     */
    class PostsListFragment : Fragment() {
        companion object {
            private val TAG = PostsListFragment::class.java.simpleName
            private const val EXTRA_POSTS_RESPONSE = "EXTRA_POSTS_RESPONSE"
            private const val EXTRA_LIST_TYPE = "EXTRA_LIST_TYPE"
            public const val LIST_POSTS = 1
            public const val LIST_DRAFT = 2
            fun newInstance(posts: PostsResponse?, listType: Int): PostsListFragment =
                PostsListFragment().apply {
                    val bundle = Bundle().apply {
                        putParcelable(EXTRA_POSTS_RESPONSE, posts)
                        putInt(EXTRA_LIST_TYPE, listType)
                    }
                    arguments = bundle
                }
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            Log.d(TAG, "onCreateView")
            return inflater.inflate(R.layout.fragment_posts_list, container, false)
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            val response = arguments!!.getParcelable<PostsResponse>(EXTRA_POSTS_RESPONSE)
            Log.d(TAG, "onActivityCreated response=$response")
            recycler_view.adapter =
                PostsAdapter(response, object : PostsAdapter.PostsClickListener {
                    override fun onClick(posts: Posts) {
                        if (activity?.isFinishing == null || activity!!.isFinishing) {
                            return
                        }
                        if (activity is PostsListActivity) {
                            val type = arguments!!.getInt(EXTRA_LIST_TYPE)
                            (activity as PostsListActivity).onClickPostsItem(posts, type)
                        }
                    }
                })
        }

        /**
         * 記事を表示するAdapter
         */
        class PostsAdapter(
            private var posts: PostsResponse? = null,
            private val listener: PostsClickListener
        ) :
            RecyclerView.Adapter<PostsAdapter.PostsViewHolder>() {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
                return PostsViewHolder.createViewHolder(parent)
            }

            override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
                holder.bindData(posts!!.items!![position], listener)
            }

            override fun getItemCount(): Int {
                if (posts == null || posts!!.items == null) {
                    return 0
                }
                if (posts!!.items!!.isEmpty()) {
                    // todo: emptyView
                }
                return posts!!.items!!.size
            }

            class PostsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                companion object {
                    fun createViewHolder(rootView: ViewGroup): PostsViewHolder =
                        PostsViewHolder(
                            LayoutInflater.from(rootView.context).inflate(
                                R.layout.include_posts_view_holder,
                                rootView, false
                            )
                        )
                }

                fun bindData(posts: Posts, listener: PostsClickListener) {
                    //todo:改善の余地あり
                    itemView.title_view.text =
                        Html.fromHtml(posts.title, Html.FROM_HTML_MODE_LEGACY).toString()
                    itemView.contents_view.text =
                        Html.fromHtml(posts.content, Html.FROM_HTML_MODE_LEGACY).toString().trim()
                            .replace("\n", " ")
                    itemView.comment_count_view.text = itemView.context.getString(
                        R.string.posts_list_comment_count,
                        posts.replies!!.totalItems.toString()
                    )

                    val date = posts.getStringDate()
                    itemView.published_view.text =
                        itemView.context.getString(R.string.posts_list_publish_date, date)
                    itemView.setOnClickListener {
                        listener.onClick(posts)
                    }
                }
            }

            interface PostsClickListener {
                fun onClick(posts: Posts)
            }
        }
    }
}