package com.mkt120.bloggerable

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.mkt120.bloggerable.model.Blogs
import com.mkt120.bloggerable.model.BlogsResponse
import kotlinx.android.synthetic.main.activity_blog_list.*
import kotlinx.android.synthetic.main.include_blog_view_holder.view.*

class BlogListActivity : AppCompatActivity() {
    companion object {
        val TAG = BlogListActivity::class.java.simpleName
    }

    var blogsResponse: BlogsResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog_list)

        recycler_view.adapter =
            BlogListAdapter(blogsResponse, object : BlogListAdapter.BlogClickListener {
                override fun onClick(blogs: Blogs) {
                    val intent =
                        PostsListActivity.createIntent(
                            this@BlogListActivity,
                            blogs.id!!,
                            blogs.name!!
                        )
                    startActivity(intent)
                }
            })

        ApiManager.getBlogs(object : ApiManager.BlogListener {
            override fun onResponse(blogList: BlogsResponse?) {
                Log.d(TAG, "list=$blogList")
                blogsResponse = blogList
                val adapter = recycler_view.adapter
                if (adapter is BlogListAdapter) {
                    adapter.setData(blogsResponse!!)
                }
                recycler_view.adapter!!.notifyDataSetChanged()
            }
        })
    }

    class BlogListAdapter(
        var blogList: BlogsResponse? = null,
        private val listener: BlogClickListener
    ) : RecyclerView.Adapter<BlogListAdapter.BlogViewHolder>() {

        fun setData(response: BlogsResponse) {
            blogList = response
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
            return BlogViewHolder.createViewHolder(parent)
        }

        override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
            if (blogList == null) {
                return
            }
            if (blogList!!.isEmpty()) {
                // todo: emptyView
                return
            }
            val items = blogList!!.items
            holder.bindData(items!![position], listener)
        }

        override fun getItemCount(): Int {
            if (blogList == null || blogList!!.isEmpty()) {
                return 0
            }
            return blogList!!.items!!.size
        }

        class BlogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            companion object {
                val TAG = BlogViewHolder::class.java.simpleName

                fun createViewHolder(rootView: ViewGroup): BlogViewHolder =
                    BlogViewHolder(
                        LayoutInflater.from(rootView.context).inflate(
                            R.layout.include_blog_view_holder,
                            rootView,
                            false
                        )
                    )
            }

            fun bindData(blogs: Blogs, listener: BlogClickListener) {
                Log.d(TAG, "bindData blogs.name=${blogs.name}")
                itemView.blog_name_view.text = blogs.name
                itemView.setOnClickListener {
                    listener.onClick(blogs)
                }
            }
        }

        interface BlogClickListener {
            fun onClick(blogs: Blogs)
        }
    }
}