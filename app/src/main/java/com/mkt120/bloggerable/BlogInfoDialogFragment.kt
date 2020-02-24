package com.mkt120.bloggerable

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.mkt120.bloggerable.model.Blogs
import java.text.SimpleDateFormat
import java.util.*

/**
 * ブログ情報ダイアログ
 */
class BlogInfoDialogFragment : DialogFragment() {

    companion object {
        private const val EXTRA_KEY_BLOG = "EXTRA_KEY_BLOG"
        fun newInstance(blogs: Blogs): BlogInfoDialogFragment =
            BlogInfoDialogFragment().apply {
                val arg = Bundle()
                arg.putParcelable(EXTRA_KEY_BLOG, blogs)
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

        val blog = arguments!!.getParcelable<Blogs>(EXTRA_KEY_BLOG)

        var titleView = TitleView(requireContext(), R.string.dialog_about_this_blog_name)
        viewGroup.addView(titleView)
        var contentView = ContentView(requireContext(), blog!!.name!!)
        viewGroup.addView(contentView)

        titleView = TitleView(requireContext(), R.string.dialog_about_this_blog_description)
        viewGroup.addView(titleView)
        contentView = ContentView(requireContext(), blog.description!!)
        viewGroup.addView(contentView)

        titleView = TitleView(requireContext(), R.string.dialog_about_this_blog_publish_date)
        viewGroup.addView(titleView)
        contentView = ContentView(
            requireContext(),
            SimpleDateFormat("yyyy/MM/dd", Locale.JAPAN).format(blog.getPublishDate())
        )
        viewGroup.addView(contentView)

        titleView = TitleView(requireContext(), R.string.dialog_about_this_blog_last_update)
        viewGroup.addView(titleView)
        contentView = ContentView(
            requireContext(),
            SimpleDateFormat("yyyy/MM/dd", Locale.JAPAN).format(blog.getLastUpdate())
        )
        viewGroup.addView(contentView)

        titleView = TitleView(requireContext(), R.string.dialog_about_this_blog_last_post_count)
        viewGroup.addView(titleView)
        contentView = ContentView(requireContext(), blog.posts!!.totalItems!!)
        viewGroup.addView(contentView)

        builder.setView(viewGroup)

        builder.setNegativeButton(R.string.close) { _, _ ->
            dismiss()
        }
        return builder.create()
    }

    class TitleView(context: Context, titleResId: Int) : TextView(context) {
        init {
            val side = context.resources.getDimensionPixelSize(R.dimen.dialog_title_padding_side)
            setPadding(side, 0, side, 0)
            textSize = 16f
            text = context.resources.getString(titleResId)
        }
    }

    class ContentView(context: Context, content: String) : TextView(context) {
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