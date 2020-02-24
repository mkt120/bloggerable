package com.mkt120.bloggerable

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.mkt120.bloggerable.api.BlogsResponse
import com.mkt120.bloggerable.model.Blogs
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.include_drawer_view.view.*

/**
 * ドロワーメニュー
 */
class DrawerView(context: Context, attr: AttributeSet?) : LinearLayout(context, attr, 0) {
    companion object {
        private val TAG = DrawerView::class.java.simpleName
    }

    constructor(context: Context) : this(context, null)

    init {
        View.inflate(context, R.layout.include_drawer_view, this)
        setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
        orientation = VERTICAL
        isClickable = true

        val clickListener = OnClickListener { view ->
            TransitionManager.beginDelayedTransition(this@DrawerView)
            place_holder.setContentId(view!!.id)
        }
        image_view_1.setOnClickListener(clickListener)
        Picasso.get().load(PreferenceManager.photoUrl).into(image_view_1)
        display_name_view.text = PreferenceManager.displayName
    }

    fun bindData(
        blogsList: BlogsResponse,
        listener: BlogListAdapter.MenuClickListener
    ) {
        Log.i(TAG, "bindData")
        recycler_view.adapter = BlogListAdapter(blogsList, listener)
        recycler_view.adapter!!.notifyDataSetChanged()
    }


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
            return if (viewType == VIEW_TYPE_TITLE) {
                TitleViewHolder.createViewHolder(parent)
            } else if (viewType == VIEW_TYPE_BLOG) {
                BlogItemViewHolder.createViewHolder(parent)
            } else {
                ContentViewHolder.createViewHolder(parent)
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
//                    holder.bindData(R.string.side_menu_account, listener)
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

                fun createViewHolder(rootView: ViewGroup): TitleViewHolder = TitleViewHolder(
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

        class BlogItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            companion object {
                val TAG = BlogItemViewHolder::class.java.simpleName

                fun createViewHolder(rootView: ViewGroup): BlogItemViewHolder = BlogItemViewHolder(
                    LayoutInflater.from(rootView.context).inflate(
                        R.layout.include_drawer_blog_item_view_holder,
                        rootView,
                        false
                    )
                )
            }

            fun bindData(blogs: Blogs, listener: MenuClickListener) {
                Log.d(TAG, "bindData blogs.name=${blogs.name}")
                if (itemView is TextView) {
                    itemView.text = blogs.name
                    itemView.setOnClickListener {
                        listener.onClick(blogs)
                    }
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
}