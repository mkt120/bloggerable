package com.mkt120.bloggerable

import android.app.Dialog
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Html
import android.text.ParcelableSpan
import android.text.Spannable
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.activity_create_post.*

/**
 * 新規投稿画面 CreatePostsScreen
 */
class CreatePostsActivity : AppCompatActivity(), Toolbar.OnMenuItemClickListener,
    AddLabelDialogFragment.OnClickListener {

    companion object {
        private const val EXTRA_KEY_BLOG_ID = "EXTRA_KEY_BLOG_ID"
        private const val RELATIVE_FONT_SIZE_X_LARGE = 2.0f
        private const val RELATIVE_FONT_SIZE_LARGE = 1.5f
        private val TAG = CreatePostsActivity::class.java.simpleName

        const val RESULT_CODE_CREATE_POSTS = 100
        const val RESULT_CODE_CREATE_DRAFT = 200

        fun createIntent(context: Context, blogId: String): Intent =
            Intent(context, CreatePostsActivity::class.java).apply {
                putExtra(EXTRA_KEY_BLOG_ID, blogId)
            }
    }

    private var dialogFragment: DialogFragment? = null
    private val labelList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        tool_bar.setTitle(R.string.create_posts_title)
        tool_bar.inflateMenu(R.menu.create_posts_menu)
        tool_bar.setOnMenuItemClickListener(this)

        button_add_bold.setOnClickListener {
            addBold()
        }

        button_add_italic.setOnClickListener {
            addItalic()
        }

        button_add_strike_through.setOnClickListener {
            addStrikeThrough()
        }

        button_add_paste.setOnClickListener {
            addPaste()
        }
        button_add_labels.setOnClickListener {
            // todo: 改善の余地あり?
            val isShowing = dialogFragment?.dialog?.isShowing
            if (isShowing != null && isShowing) {
                return@setOnClickListener
            }
            dialogFragment = AddLabelDialogFragment.newInstance()
            dialogFragment!!.show(supportFragmentManager, null)
        }

        button_history.setOnClickListener {
            val isShowing = dialogFragment?.dialog?.isShowing
            if (isShowing != null && isShowing) {
                return@setOnClickListener
            }
            dialogFragment = AddLabelHistoryDialogFragment.newInstance()
            dialogFragment!!.show(supportFragmentManager, null)
        }
        button_add_font_change.setOnClickListener {
            addRelativeSize()
        }
    }

    override fun addLabel(label: String) {
        if (labelList.contains(label)) {
            Toast.makeText(this@CreatePostsActivity, "already inserted.", Toast.LENGTH_SHORT).show()
            return
        }
        val view = LabelView(this@CreatePostsActivity).apply {
            text = label
            setOnClickListener {
                label_view.removeView(this)
                labelList.remove(label)
            }
        }
        label_view.addView(view)
        labelList.add(label)

        horizontal_scroll.post {
            horizontal_scroll.scrollTo(view.right, 0)
        }
    }

    /**
     * boldを追加・削除する
     */
    private fun addBold() {
        Log.i(TAG, "addBold")
        val cursorStart = Math.min(edit_text_contents.selectionStart, edit_text_contents.selectionEnd)
        val cursorEnd = Math.max(edit_text_contents.selectionStart, edit_text_contents.selectionEnd)
        val spans = edit_text_contents.text.getSpans(cursorStart, cursorEnd, StyleSpan::class.java)
        if (spans.isEmpty()) {
            // boldをつけるだけ
            addSpan(StyleSpan(Typeface.BOLD))
            edit_text_contents.setSelection(cursorStart, cursorEnd)
            return
        }
        val hasBold = spans.any  { (it.style != Typeface.ITALIC) }
        if (!hasBold) {
            // boldをつけるだけ
            addSpan(StyleSpan(Typeface.BOLD))
            edit_text_contents.setSelection(cursorStart, cursorEnd)
            return
        }

        // boldを外すだけ
        for (span in spans) {
            if (span.style == Typeface.ITALIC) {
                continue
            }
            val spanLeft = Math.min(edit_text_contents.text.getSpanStart(span), edit_text_contents.text.getSpanEnd(span))
            val spanRight = Math.max(edit_text_contents.text.getSpanStart(span), edit_text_contents.text.getSpanEnd(span))
            edit_text_contents.text.removeSpan(span)
            if (spanLeft < cursorStart || cursorEnd < spanRight) {
                // 選択範囲が1つのspanに含まれている
                var clone = StyleSpan(span.style)
                if (spanLeft < cursorStart) {
                    // 左が出てる 左にspanつけなおし
                    addSpan(clone, spanLeft, cursorStart)
                    clone = StyleSpan(span.style)
                }
                if (cursorEnd < spanRight) {
                    // 右が出てる 右にspanつけなおし
                    addSpan(clone, cursorEnd, spanRight)
                }
            }
        }
        edit_text_contents.setSelection(cursorStart, cursorEnd)
    }

    /**
     * Italicを追加・削除する
     */
    private fun addItalic() {
        Log.i(TAG, "addItalic")
        val cursorStart = Math.min(edit_text_contents.selectionStart, edit_text_contents.selectionEnd)
        val cursorEnd = Math.max(edit_text_contents.selectionStart, edit_text_contents.selectionEnd)
        val spans = edit_text_contents.text.getSpans(cursorStart, cursorEnd, StyleSpan::class.java)
        if (spans.isEmpty()) {
            // italicをつけるだけ
            addSpan(StyleSpan(Typeface.ITALIC))
            edit_text_contents.setSelection(cursorStart, cursorEnd)
            return
        }
        val hasBold = spans.any  { (it.style != Typeface.BOLD) }
        if (!hasBold) {
            // italicをつけるだけ
            addSpan(StyleSpan(Typeface.ITALIC))
            edit_text_contents.setSelection(cursorStart, cursorEnd)
            return
        }

        // italic外すだけ
        for (span in spans) {
            if (span.style == Typeface.BOLD) {
                continue
            }
            val spanLeft = Math.min(edit_text_contents.text.getSpanStart(span), edit_text_contents.text.getSpanEnd(span))
            val spanRight = Math.max(edit_text_contents.text.getSpanStart(span), edit_text_contents.text.getSpanEnd(span))
            edit_text_contents.text.removeSpan(span)
            if (spanLeft < cursorStart || cursorEnd < spanRight) {
                // 選択範囲が1つのspanに含まれている
                if (spanLeft < cursorStart) {
                    // 左が出てる 左にspanつけなおし
                    val clone = StyleSpan(span.style)
                    addSpan(clone, spanLeft, cursorStart)
                }
                if (cursorEnd < spanRight) {
                    // 右が出てる 右にspanつけなおし
                    val clone = StyleSpan(span.style)
                    addSpan(clone, cursorEnd, spanRight)
                }
            }
        }
        edit_text_contents.setSelection(cursorStart, cursorEnd)
    }

    /**
     * 打消し線を追加・削除する
     */
    private fun addStrikeThrough() {
        Log.i(TAG, "addStrikeThrough")
        val cursorStart = Math.min(edit_text_contents.selectionStart, edit_text_contents.selectionEnd)
        val cursorEnd = Math.max(edit_text_contents.selectionStart, edit_text_contents.selectionEnd)
        val spans = edit_text_contents.text.getSpans(cursorStart, cursorEnd, StrikethroughSpan::class.java)
        if (spans.isEmpty()) {
            addSpan(StrikethroughSpan())
            edit_text_contents.setSelection(cursorStart, cursorEnd)
            return
        }
        for (span in spans) {
            val spanLeft = Math.min(edit_text_contents.text.getSpanStart(span), edit_text_contents.text.getSpanEnd(span))
            val spanRight = Math.max(edit_text_contents.text.getSpanStart(span), edit_text_contents.text.getSpanEnd(span))
            edit_text_contents.text.removeSpan(span)
            if (spanLeft < cursorStart || cursorEnd < spanRight) {
                // 選択範囲が1つのspanに含まれている
                if (spanLeft < cursorStart) {
                    // 左が出てる 左にspanつけなおし
                    val clone = StrikethroughSpan()
                    addSpan(clone, spanLeft, cursorStart)
                }
                if (cursorEnd < spanRight) {
                    val clone = StrikethroughSpan()
                    // 右が出てる 右にspanつけなおし
                    addSpan(clone, cursorEnd, spanRight)
                }
            }
        }
        edit_text_contents.setSelection(cursorStart, cursorEnd)
    }

    /**
     * クリップボードにあるテキストを張り付ける
     */
    private fun addPaste() {
        Log.i(TAG, "addPaste")
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (clipboard.primaryClip == null) {
            return
        }
        val item = clipboard.primaryClip!!.getItemAt(0)
        val left = Math.min(edit_text_contents.selectionStart, edit_text_contents.selectionEnd)
        val right = Math.max(edit_text_contents.selectionStart, edit_text_contents.selectionEnd)
        val clip = item.text.toString()
        edit_text_contents.text.replace(left, right, clip)
        edit_text_contents.setSelection(left + clip.length)
    }

    /**
     * spanを追加する
     */
    private fun addSpan(span: ParcelableSpan) {
        val left = Math.min(edit_text_contents.selectionStart, edit_text_contents.selectionEnd)
        val right = Math.max(edit_text_contents.selectionStart, edit_text_contents.selectionEnd)
        addSpan(span, left, right)
    }

    /**
     * spanを追加する
     */
    private fun addSpan(span: ParcelableSpan, left:Int, right:Int) {
        Log.i("CreatePostsActivity", "addSpan left=$left, right=$right, span=$span")
        val spannable :Spannable = edit_text_contents.text
        spannable.setSpan(span, left, right, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        edit_text_contents.setText(spannable)
    }

    /**
     * 文字サイズを変更する
     */
    private fun addRelativeSize() {
        Log.i(TAG, "addRelativeSize")
        val cursorLeft = Math.min(edit_text_contents.selectionStart, edit_text_contents.selectionEnd)
        val cursorRight = Math.max(edit_text_contents.selectionStart, edit_text_contents.selectionEnd)
        val spans = edit_text_contents.text.getSpans(cursorLeft, cursorRight, RelativeSizeSpan::class.java)
        if (spans.isEmpty()) {
            addSpan(RelativeSizeSpan(RELATIVE_FONT_SIZE_LARGE))
            edit_text_contents.setSelection(cursorLeft, cursorRight)
            return
        }
        if (spans.size == 1) {
            val spanLeft = Math.min(edit_text_contents.text.getSpanStart(spans[0]), edit_text_contents.text.getSpanEnd(spans[0]))
            val spanRight = Math.max(edit_text_contents.text.getSpanStart(spans[0]), edit_text_contents.text.getSpanEnd(spans[0]))
            if (spanLeft == cursorLeft && spanRight == cursorRight) {
                // 完全に一致
                edit_text_contents.text.removeSpan(spans[0])
                if (spans[0].sizeChange == RELATIVE_FONT_SIZE_LARGE) {
                    addSpan(RelativeSizeSpan(RELATIVE_FONT_SIZE_X_LARGE), cursorLeft, cursorRight)
                }
                edit_text_contents.setSelection(cursorLeft, cursorRight)
                return
            }
        }

        // それ以外は一番大きいサイズに合わせてspanを付与
        for (span in spans) {
            val spanLeft = Math.min(edit_text_contents.text.getSpanStart(span), edit_text_contents.text.getSpanEnd(span))
            val spanRight = Math.max(edit_text_contents.text.getSpanStart(span), edit_text_contents.text.getSpanEnd(span))
            edit_text_contents.text.removeSpan(span)
            if (spanLeft < cursorLeft || cursorRight < spanRight) {
                // 選択範囲が1つのspanに含まれている
                if (spanLeft < cursorLeft) {
                    // 左が出てる 左にspanつけなおし
                    addSpan(RelativeSizeSpan(span.sizeChange), spanLeft, cursorLeft)
                }
                if (cursorRight < spanRight) {
                    // 右が出てる 右にspanつけなおし
                    addSpan(RelativeSizeSpan(span.sizeChange), cursorRight, spanRight)
                }
            }
        }
        val largestSize = spans.maxBy { it.sizeChange }!!.sizeChange
        addSpan(RelativeSizeSpan(largestSize), cursorLeft, cursorRight)
        edit_text_contents.setSelection(cursorLeft, cursorRight)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item!!.itemId){
            R.id.create_posts -> {
                createPosts(false)
                return true
            }
            R.id.upload_as_draft -> {
                createPosts(true)
            }

        }
        return false
    }

    private fun createPosts(isDraft : Boolean) {
        val blogId = intent.getStringExtra(EXTRA_KEY_BLOG_ID)!!
        val title = edit_text_title.text.toString()

        if (!isDraft && title.isEmpty()) {
            // 空 empty title
            Toast.makeText(
                this@CreatePostsActivity,
                R.string.toast_error_create_posts_no_title,
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val html = Html.toHtml(edit_text_contents.text, Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL)
        ApiManager.createPosts(
            blogId,
            title,
            html,
            createLabels(),
            isDraft,
            object : ApiManager.CompleteListener {
                override fun onComplete() {
                    val messageResId : Int = if (isDraft) {
                        R.string.toast_create_draft_success
                    } else {
                        R.string.toast_create_posts_success
                    }
                    Toast.makeText(
                        this@CreatePostsActivity,
                        messageResId,
                        Toast.LENGTH_SHORT
                    ).show()
                    if (isDraft) {
                        setResult(RESULT_CODE_CREATE_DRAFT)
                    } else {
                        setResult(RESULT_CODE_CREATE_POSTS)
                    }
                    finish()
                }

                override fun onFailed(t: Throwable) {
                    Toast.makeText(
                        this@CreatePostsActivity,
                        R.string.toast_create_posts_failed,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun createLabels(): MutableList<String>? {
        if (label_view.childCount == 0) {
            return null
        }
        val labels: MutableList<String> = mutableListOf()
        for (i in 0..label_view.childCount) {
            val view = label_view.getChildAt(i)
            if (view is TextView) {
                val label = view.text
                labels.add(label.toString())
            }
        }
        return labels
    }

    override fun onBackPressed() {
        if (edit_text_contents.text.isNotEmpty()) {
            val confirmDialog: ConfirmDialog = ConfirmDialog.newInstance()
            confirmDialog.show(supportFragmentManager, null)
            return
        }
        super.onBackPressed()
    }

    fun onPositiveClick() {
        createPosts(true)
    }
    fun onNegativeClick() {
        finish()
    }

    class ConfirmDialog : DialogFragment() {

        companion object {
            fun newInstance() : ConfirmDialog = ConfirmDialog()
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage(R.string.create_posts_dialog_message)
                .setPositiveButton(R.string.create_posts_dialog_positive_button) { _, _  ->
                    if (activity is CreatePostsActivity) {
                        (activity as CreatePostsActivity).onPositiveClick()
                    }
                    dismiss()
                }
                .setNegativeButton(R.string.create_posts_dialog_negative_button) { _, _ ->
                    if (activity is CreatePostsActivity) {
                        (activity as CreatePostsActivity).onNegativeClick()
                    }
                    dismiss()
                }
                .setNeutralButton(android.R.string.cancel, null)
            return builder.create()
        }
    }
}