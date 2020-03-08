package com.mkt120.bloggerable.top.posts

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mkt120.bloggerable.api.PostsResponse
import com.mkt120.bloggerable.model.Posts
import com.mkt120.bloggerable.top.posts.item.PostsItemViewHolder


/**
 * 記事を表示するAdapter
 */
class PostsAdapter(
    private var posts: PostsResponse? = null,
    private val listener: PostsClickListener
) :
    RecyclerView.Adapter<PostsItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsItemViewHolder {
        return PostsItemViewHolder.createViewHolder(
            parent
        )
    }

    override fun onBindViewHolder(holder: PostsItemViewHolder, position: Int) {
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


    interface PostsClickListener {
        fun showItem(posts: Posts)
    }
}