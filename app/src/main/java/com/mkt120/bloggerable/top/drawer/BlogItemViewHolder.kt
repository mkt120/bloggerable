package com.mkt120.bloggerable.top.drawer

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mkt120.bloggerable.R
import com.mkt120.bloggerable.model.Blogs

class BlogItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    DrawerContract.BlogsItemView {
    private val presenter: DrawerContract.BlogsItemPresenter

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

    fun bindData(blogs: Blogs, listener: DrawerView.BlogListAdapter.MenuClickListener) {
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

    class BlogsItemPresenter(val view: DrawerContract.BlogsItemView) :
        DrawerContract.BlogsItemPresenter {
        override fun onBindData(blogs: Blogs) {
            view.setBlogName(blogs.name!!)
        }
    }
}
