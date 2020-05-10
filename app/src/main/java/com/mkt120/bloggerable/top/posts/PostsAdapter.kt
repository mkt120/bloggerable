package com.mkt120.bloggerable.top.posts

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mkt120.bloggerable.model.posts.Posts
import com.mkt120.bloggerable.top.TopContract
import com.mkt120.bloggerable.top.posts.item.PostsItemViewHolder

/**
 * 記事を表示するAdapter
 */
class PostsAdapter(
    private var type: TopContract.TYPE,
    private var posts: List<Posts>? = null,
    private val listener: PostsClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_POST_VIEW = 1
        private const val TYPE_EMPTY_VIEW = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_POST_VIEW) {
            PostsItemViewHolder.createViewHolder(parent)
        } else {
            EmptyViewHolder.newInstance(parent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (posts != null && posts!!.isEmpty()) {
            TYPE_EMPTY_VIEW
        } else {
            TYPE_POST_VIEW
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PostsItemViewHolder) {
            holder.bindData(posts!![position], listener)
        }
        if (holder is EmptyViewHolder) {
            holder.bindData(type)
        }
    }

    override fun getItemCount(): Int {
        if (posts == null) {
            return 0
        }
        if (posts!!.isEmpty()) {
            return 1
        }
        return posts!!.size
    }


    interface PostsClickListener {
        fun showItem(posts: Posts)
    }
}