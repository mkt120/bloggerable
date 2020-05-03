package com.mkt120.bloggerable.top.drawer

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mkt120.bloggerable.R
import com.mkt120.bloggerable.model.blogs.Blogs

class BlogItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    DrawerContract.BlogItemView {
    private val presenter: DrawerContract.BlogItemPresenter

    companion object {
        val TAG = BlogItemViewHolder::class.java.simpleName

        fun createViewHolder(rootView: ViewGroup): BlogItemViewHolder =
            BlogItemViewHolder(
                LayoutInflater.from(rootView.context).inflate(
                    R.layout.include_drawer_blog_item_view_holder,
                    rootView,
                    false
                )
            )
    }

    init {
        presenter =
            BlogsItemPresenter(
                this@BlogItemViewHolder
            )
    }

    fun bindData(blogs: Blogs, listener: BlogListAdapter.MenuClickListener) {
        Log.d(TAG, "onBindData blogs.name=${blogs.name}")
        presenter.onBindData(blogs)
        itemView.setOnClickListener {
            listener.onClick(blogs)
        }
    }

    override fun setBlogName(name: String) {
        if (itemView is TextView) {
            itemView.text = name
        }
    }

    class BlogsItemPresenter(val view: DrawerContract.BlogItemView) :
        DrawerContract.BlogItemPresenter {
        override fun onBindData(blog: Blogs) {
            view.setBlogName(blog.name!!)
        }
    }
}
