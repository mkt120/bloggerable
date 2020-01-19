package com.mkt120.bloggerable

import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataSet
import kotlinx.android.synthetic.main.activity_create_post.*

/**
 * 新規投稿画面 CreatePostsScreen
 */
class CreatePostsActivity : AppCompatActivity(), Toolbar.OnMenuItemClickListener,
    AddLabelDialogFragment.OnClickListener {

    companion object {
        private const val EXTRA_KEY_BLOG_ID = "EXTRA_KEY_BLOG_ID"

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

    private fun addBold() {
        val text = edit_text_contents.text
        val start = edit_text_contents.selectionStart
        val end = edit_text_contents.selectionEnd
        val middle: String = text.substring(start, end)
        add("**$middle**", start + middle.length + 2)
    }

    private fun addItalic() {
        val text = edit_text_contents.text
        val start = edit_text_contents.selectionStart
        val end = edit_text_contents.selectionEnd
        val middle: String = text.substring(start, end)
        add("*$middle*", start + middle.length + 1)
    }

    private fun addStrikeThrough() {
        val text = edit_text_contents.text
        val start = edit_text_contents.selectionStart
        val end = edit_text_contents.selectionEnd
        val middle: String = text.substring(start, end)
        add("~~$middle~~", start + middle.length + 2)
    }

    private fun addPaste() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (clipboard.primaryClip == null) {
            return
        }
        val item = clipboard.primaryClip!!.getItemAt(0)
        val start = edit_text_contents.selectionStart
        add(item.text.toString(), start + item.text.length)
    }

    private fun add(
        middle: String,
        selection: Int
    ) {
        val start = edit_text_contents.selectionStart
        val end = edit_text_contents.selectionEnd

        val text = edit_text_contents.text
        val former = text.substring(0, start)
        val latter = text.substring(end)

        edit_text_contents.setText("$former$middle$latter")
        edit_text_contents.setSelection(selection)
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
        val content = edit_text_contents.text.toString()

        if (!isDraft && title.isEmpty()) {
            // 空 empty title
            Toast.makeText(
                this@CreatePostsActivity,
                R.string.toast_error_create_posts_no_title,
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Markdown to HTML
        val options = MutableDataSet()
        options.set(
            Parser.EXTENSIONS,
            listOf(TablesExtension.create(), StrikethroughExtension.create())
        )
        val parser = Parser.builder(options).build()
        val renderer = HtmlRenderer.builder(options).build()
        val document = parser.parse(content)
        val html = renderer.render(document)

        // create Markdown string
        // val markdown = FlexmarkHtmlConverter.builder(options).build().convert(html)

        ApiManager.createPosts(
            blogId,
            title,
            html,
            createLabels(),
            isDraft,
            object : ApiManager.CompleteListener {
                override fun onComplete() {
                    val messageResId : Int = if (isDraft) {
                        R.string.toast_create_posts_success
                    } else {
                        R.string.toast_create_posts_success
                    }
                    Toast.makeText(
                        this@CreatePostsActivity,
                        messageResId,
                        Toast.LENGTH_SHORT
                    ).show()
                    setResult(Activity.RESULT_OK)
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

}