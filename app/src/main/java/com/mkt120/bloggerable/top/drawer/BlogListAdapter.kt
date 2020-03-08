package com.mkt120.bloggerable.top.drawer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mkt120.bloggerable.R
import com.mkt120.bloggerable.api.BlogsResponse
import com.mkt120.bloggerable.model.Blogs

class BlogListAdapter(
    private var blogList: BlogsResponse? = null,
    private val listener: MenuClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val VIEW_TYPE_TITLE = 1
        private const val VIEW_TYPE_BLOG = 2
        private const val VIEW_TYPE_CONTENT = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_TITLE -> TitleViewHolder.createViewHolder(
                parent
            )
            VIEW_TYPE_BLOG -> BlogItemViewHolder.createViewHolder(
                parent
            )
            else -> ContentViewHolder.createViewHolder(
                parent
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TitleViewHolder) {
            holder.bindData(R.string.drawer_menu_blog_list)
        }
        var position = position - 1
        if (holder is BlogItemViewHolder) {
            if (blogList == null) {
                return
            }
            if (blogList!!.isEmpty()) {
                // todo: emptyView
                return
            }
            val items = blogList!!.items
            holder.bindData(items!![position], listener)

        } else if (holder is ContentViewHolder) {
            blogList?.let {
                position -= it.items!!.size
            }
            if (position == 0) {
//                    holder.onBindData(R.string.side_menu_account, listener)
//                } else {
                holder.bindData(R.string.drawer_menu_about_this_app, listener)
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return VIEW_TYPE_TITLE
        }
        if (blogList == null || blogList!!.isEmpty()) {
            return VIEW_TYPE_CONTENT
        }
        val position = position - 1
        return if (position < blogList!!.items!!.size) {
            VIEW_TYPE_BLOG
        } else {
            VIEW_TYPE_CONTENT
        }
    }

    override fun getItemCount(): Int {
        if (blogList == null || blogList!!.isEmpty()) {
            return 0
        }
        return blogList!!.items!!.size + 2
    }

    class TitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        companion object {
            val TAG = TitleViewHolder::class.java.simpleName

            fun createViewHolder(rootView: ViewGroup): TitleViewHolder =
                TitleViewHolder(
                    LayoutInflater.from(rootView.context).inflate(
                        R.layout.include_drawer_title_section,
                        rootView,
                        false
                    )
                )
        }

        fun bindData(titleResId: Int) {
            if (itemView is TextView) {
                itemView.setText(titleResId)
            }
        }
    }

    class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        companion object {
            val TAG = ContentViewHolder::class.java.simpleName

            fun createViewHolder(rootView: ViewGroup): ContentViewHolder =
                ContentViewHolder(
                    LayoutInflater.from(rootView.context).inflate(
                        R.layout.include_drawer_content_view_holder,
                        rootView,
                        false
                    )
                )
        }

        fun bindData(titleResId: Int, listener: MenuClickListener) {
            if (itemView is TextView) {
                itemView.setText(titleResId)
                itemView.setOnClickListener {
                    listener.onClick(titleResId)
                }
            }
        }
    }

    interface MenuClickListener {
        fun onClick(itemResId: Int)
        fun onClick(blogs: Blogs)
    }
}