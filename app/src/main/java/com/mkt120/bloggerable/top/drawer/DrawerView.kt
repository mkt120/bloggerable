package com.mkt120.bloggerable.top.drawer

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.transition.TransitionManager
import com.mkt120.bloggerable.R
import com.mkt120.bloggerable.datasource.BloggerApiDataSource
import com.mkt120.bloggerable.datasource.PreferenceDataSource
import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.repository.AccountRepository
import com.mkt120.bloggerable.usecase.GetAllAccount
import com.mkt120.bloggerable.usecase.GetCurrentAccount
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.include_drawer_view.view.*

/**
 * ドロワーメニュー
 */
class DrawerView(context: Context, attr: AttributeSet?) : LinearLayout(context, attr, 0),
    DrawerContract.View {

    companion object {
        private val TAG = DrawerView::class.java.simpleName
    }

    constructor(context: Context) : this(context, null)

    private var presenter: DrawerContract.Presenter

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

        val bloggerApiDataSource = BloggerApiDataSource()
        val preferenceDataSource = PreferenceDataSource()
        val accountRepository = AccountRepository(bloggerApiDataSource, preferenceDataSource)
        val getAllAccount = GetAllAccount(accountRepository)
        val getGoogleAccount = GetCurrentAccount(accountRepository)
        presenter = DrawerPresenter(this@DrawerView, getAllAccount, getGoogleAccount)
        presenter.initialize()
    }

    override fun setName(name: String) {
        display_name_view.text = name
    }

    override fun setImage(url: String) {
        Picasso.get().load(url).into(image_view_1)
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