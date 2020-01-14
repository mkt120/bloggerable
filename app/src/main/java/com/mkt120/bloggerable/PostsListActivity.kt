package com.mkt120.bloggerable

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.mkt120.bloggerable.api.PostsResponse
import com.mkt120.bloggerable.model.Posts
import kotlinx.android.synthetic.main.activity_posts_list.*
import kotlinx.android.synthetic.main.include_posts_view_holder.view.*

class PostsListActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_KEY_BLOG_ID = "EXTRA_KEY_BLOG_ID"
        private const val EXTRA_KEY_BLOG_NAME = "EXTRA_KEY_BLOG_NAME"
        private const val REQUEST_CODE_CREATE_POST = 100
        private const val REQUEST_CODE_DELETE_POST = 200

        fun createIntent(context: Context, blogId: String, name: String): Intent =
            Intent(context, PostsListActivity::class.java).apply {
                putExtra(EXTRA_KEY_BLOG_ID, blogId)
                putExtra(EXTRA_KEY_BLOG_NAME, name)
            }
    }

    var response: PostsResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts_list)
        recycler_view.adapter = PostsAdapter(response, object : PostsAdapter.PostsClickListener {
            override fun onClick(posts: Posts) {
                val blogId = intent.getStringExtra(EXTRA_KEY_BLOG_ID)
                val i = PostsDetailActivity.createIntent(this@PostsListActivity, blogId!!, posts)
                startActivityForResult(i, REQUEST_CODE_DELETE_POST)
            }
        })
        tool_bar.title = intent.getStringExtra(EXTRA_KEY_BLOG_NAME)

        fab.setOnClickListener {
            val blogId = intent.getStringExtra(EXTRA_KEY_BLOG_ID)
            val intent = CreatePostsActivity.createIntent(this@PostsListActivity, blogId)
            startActivityForResult(intent, REQUEST_CODE_CREATE_POST)
        }

        requestPosts()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CREATE_POST || requestCode == REQUEST_CODE_DELETE_POST) {
            if (resultCode == Activity.RESULT_OK) {
                requestPosts()
            }
        }
    }

    private fun requestPosts() {
        val blogId = intent.getStringExtra(EXTRA_KEY_BLOG_ID)
        ApiManager.getPosts(blogId!!, object : ApiManager.PostsListener {
            override fun onResponse(posts: PostsResponse?) {
                response = posts
                val adapter = recycler_view.adapter
                if (adapter is PostsAdapter) {
                    adapter.setData(posts!!)
                    adapter.notifyDataSetChanged()
                }
            }
        })
    }

    class PostsAdapter(var posts: PostsResponse? = null, private val listener: PostsClickListener) :
        RecyclerView.Adapter<PostsAdapter.PostsViewHolder>() {

        fun setData(posts: PostsResponse) {
            this.posts = posts
        }

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
                itemView.title_view.text = Html.fromHtml(posts.title, 0)
                itemView.contents_view.text = Html.fromHtml(posts.content, 0)
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