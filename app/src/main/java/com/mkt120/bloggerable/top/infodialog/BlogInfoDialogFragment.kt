package com.mkt120.bloggerable.top.infodialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.DialogFragment
import com.mkt120.bloggerable.R
import com.mkt120.bloggerable.model.blogs.Blogs
import java.text.SimpleDateFormat
import java.util.*

/**
 * ブログ情報ダイアログ
 */
class BlogInfoDialogFragment : DialogFragment() {

    companion object {
        private const val EXTRA_KEY_BLOG_TITLE = "EXTRA_KEY_BLOG_TITLE"
        private const val EXTRA_KEY_BLOG_DESCRIPTION = "EXTRA_KEY_BLOG_DESCRIPTION"
        private const val EXTRA_KEY_BLOG_PUBLISH_DATE = "EXTRA_KEY_BLOG_PUBLISH_DATE"
        private const val EXTRA_KEY_BLOG_LAST_UPDATE = "EXTRA_KEY_BLOG_LAST_UPDATE"
        private const val EXTRA_KEY_BLOG_POST_COUNT = "EXTRA_KEY_BLOG_POST_COUNT"
        fun newInstance(blogs: Blogs): BlogInfoDialogFragment =
            BlogInfoDialogFragment().apply {
                val arg = Bundle()
                arg.putString(EXTRA_KEY_BLOG_TITLE, blogs.name!!)
                arg.putString(EXTRA_KEY_BLOG_DESCRIPTION, blogs.description!!)
                arg.putSerializable(EXTRA_KEY_BLOG_PUBLISH_DATE, blogs.getPublishDate())
                arg.putSerializable(EXTRA_KEY_BLOG_LAST_UPDATE, blogs.getLastUpdate())
                arg.putString(EXTRA_KEY_BLOG_POST_COUNT, blogs.posts!!.totalItems!!.toString())
                arguments = arg
            }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(R.string.dialog_about_this_blog_title)
        val viewGroup = LinearLayout(requireContext())
        viewGroup.orientation = LinearLayout.VERTICAL
        val padding = context!!.resources.getDimensionPixelSize(R.dimen.dialog_view_group_padding)
        viewGroup.setPadding(0, padding, 0, padding)

        val title = arguments!!.getString(EXTRA_KEY_BLOG_TITLE)
        var titleView = TitleView(
            requireContext(),
            R.string.dialog_about_this_blog_name
        )
        viewGroup.addView(titleView)
        var contentView = ContentView(requireContext(), title)
        viewGroup.addView(contentView)

        val description = arguments!!.getString(EXTRA_KEY_BLOG_DESCRIPTION)
        titleView = TitleView(requireContext(), R.string.dialog_about_this_blog_description)
        viewGroup.addView(titleView)
        contentView = ContentView(requireContext(), description)
        viewGroup.addView(contentView)

        val publishDate = arguments!!.getSerializable(EXTRA_KEY_BLOG_PUBLISH_DATE) as Date
        titleView = TitleView(
            requireContext(),
            R.string.dialog_about_this_blog_publish_date
        )
        viewGroup.addView(titleView)
        contentView = ContentView(
            requireContext(),
            SimpleDateFormat("yyyy/MM/dd", Locale.JAPAN).format(publishDate)
        )
        viewGroup.addView(contentView)

        titleView = TitleView(
            requireContext(),
            R.string.dialog_about_this_blog_last_update
        )
        viewGroup.addView(titleView)

        val lastUpdate = arguments!!.getSerializable(EXTRA_KEY_BLOG_LAST_UPDATE) as Date
        contentView = ContentView(
            requireContext(),
            SimpleDateFormat("yyyy/MM/dd", Locale.JAPAN).format(lastUpdate)
        )
        viewGroup.addView(contentView)

        titleView = TitleView(
            requireContext(),
            R.string.dialog_about_this_blog_last_post_count
        )
        val postCount = arguments!!.getString(EXTRA_KEY_BLOG_POST_COUNT)

        viewGroup.addView(titleView)
        contentView = ContentView(requireContext(), postCount)
        viewGroup.addView(contentView)

        builder.setView(viewGroup)

        builder.setNegativeButton(R.string.close) { _, _ ->
            dismiss()
        }
        return builder.create()
    }

    private class TitleView(context: Context, titleResId: Int) : AppCompatTextView(context) {
        init {
            val side = context.resources.getDimensionPixelSize(R.dimen.dialog_title_padding_side)
            setPadding(side, 0, side, 0)
            textSize = 16f
            text = context.resources.getString(titleResId)
        }
    }

    private class ContentView(context: Context, content: String?) : AppCompatTextView(context) {
        init {
            val side = context.resources.getDimensionPixelSize(R.dimen.dialog_content_padding_side)
            val top = context.resources.getDimensionPixelSize(R.dimen.dialog_content_padding_top)
            val bottom =
                context.resources.getDimensionPixelSize(R.dimen.dialog_content_padding_bottom)
            setPadding(side, top, side, bottom)
            textSize = 14f
            text = content
        }
    }
}