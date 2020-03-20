package com.mkt120.bloggerable.top.drawer

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.transition.TransitionManager
import com.mkt120.bloggerable.PreferenceManager
import com.mkt120.bloggerable.R
import com.mkt120.bloggerable.api.BlogsResponse
import com.mkt120.bloggerable.model.blogs.Blogs
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

    fun onBindData(
        blogsList: List<Blogs>,
        listener: BlogListAdapter.MenuClickListener
    ) {
        Log.i(TAG, "onBindData")
        recycler_view.adapter =
            BlogListAdapter(blogsList, listener)
        recycler_view.adapter!!.notifyDataSetChanged()
    }
}