package com.mkt120.bloggerable.top.posts.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mkt120.bloggerable.R
import com.mkt120.bloggerable.model.posts.Posts
import com.mkt120.bloggerable.top.posts.PostsAdapter
import kotlinx.android.synthetic.main.include_posts_view_holder.view.*

class PostsItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    PostsItemContract.PostsItemView {

    private val presenter: PostsItemContract.PostsItemPresenter

    init {
        presenter =
            PostsItemPresenter(this@PostsItemViewHolder)
    }

    companion object {
        fun createViewHolder(rootView: ViewGroup): PostsItemViewHolder =
            PostsItemViewHolder(
                LayoutInflater.from(rootView.context).inflate(
                    R.layout.include_posts_view_holder,
                    rootView, false
                )
            )
    }

    fun bindData(posts: Posts, listener: PostsAdapter.PostsClickListener) {
        presenter.onBindData(posts)

        itemView.setOnClickListener {
            listener.showItem(posts)
        }
    }

    override fun setContent(content: String) {
        itemView.contents_view.text = content
    }

    override fun setCommentCount(count: String) {
        itemView.comment_count_view.text = itemView.context.getString(
            R.string.posts_list_comment_count, count
        )
    }

    override fun setPublishDate(date: String) {
        itemView.published_view.text =
            itemView.context.getString(R.string.posts_list_publish_date, date)
    }

    override fun setTitle(title: String) {
        itemView.title_view.text = title
    }

}
