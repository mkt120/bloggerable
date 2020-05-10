package com.mkt120.bloggerable.top.posts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mkt120.bloggerable.R
import com.mkt120.bloggerable.top.TopContract
import kotlinx.android.synthetic.main.include_empty_view_holder.view.*

class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        fun newInstance(rootView: ViewGroup) : EmptyViewHolder =
            EmptyViewHolder(LayoutInflater.from(rootView.context).inflate(R.layout.include_empty_view_holder, rootView, false))
    }

    fun bindData(type: TopContract.TYPE) {
        itemView.text_view.setText(type.textResId)
    }
}