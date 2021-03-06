package com.mkt120.bloggerable.top

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.mkt120.bloggerable.R
import com.mkt120.bloggerable.top.posts.PostsListFragment

/**
 * 記事一覧(投稿・下書き)画面をそれぞれ表示するPagerAdapter
 */
class PostsPagerAdapter(
    private var context: Context,
    private var blogsId: String?,
    fragmentManager: FragmentManager
) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    companion object {
        private val PAGE_TITLES =
            arrayOf(
                R.string.posts_list_title_posts,
                R.string.posts_list_title_draft
            )
    }

    override fun getItem(position: Int): Fragment {
        Log.d("PostsPagerAdapter", "getItem position=$position")
        return if (position == 0) {
            PostsListFragment.newInstance(
                blogsId,
                TopContract.TYPE.POST
            )
        } else {
            PostsListFragment.newInstance(
                blogsId,
                TopContract.TYPE.DRAFT
            )
        }
    }

    fun updateCurrentBlog(blogsId: String) {
        this.blogsId = blogsId
        notifyDataSetChanged()
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun getCount(): Int {
        return PAGE_TITLES.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.getString(PAGE_TITLES[position])
    }
}

