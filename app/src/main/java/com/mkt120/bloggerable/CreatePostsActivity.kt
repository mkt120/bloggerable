package com.mkt120.bloggerable

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataSet
import kotlinx.android.synthetic.main.activity_create_post.*

/**
 * 新規投稿画面 CreatePostsScreen
 */
class CreatePostsActivity : AppCompatActivity(), Toolbar.OnMenuItemClickListener {

    companion object {
        private const val EXTRA_KEY_BLOG_ID = "EXTRA_KEY_BLOG_ID"

        fun createIntent(context: Context, blogId: String): Intent =
            Intent(context, CreatePostsActivity::class.java).apply {
                putExtra(EXTRA_KEY_BLOG_ID, blogId)
            }
    }

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
        when {
            item?.itemId == R.id.create_posts -> {
                createPosts()
                return true
            }
        }
        return false
    }

    private fun createPosts() {
        val blogId = intent.getStringExtra(EXTRA_KEY_BLOG_ID)!!
        val title = edit_text_title.text.toString()
        val content = edit_text_contents.text.toString()

        if (title.isEmpty()) {
            // 空 empty title
            Toast.makeText(this@CreatePostsActivity, R.string.toast_error_create_posts_no_title, Toast.LENGTH_SHORT).show()
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

        ApiManager.createPosts(blogId, title, html, object : ApiManager.CompleteListener {
            override fun onComplete() {
                Toast.makeText(this@CreatePostsActivity, R.string.toast_create_posts_success, Toast.LENGTH_SHORT).show()
                finish()
            }

            override fun onFailed(t: Throwable) {
                Toast.makeText(this@CreatePostsActivity, R.string.toast_create_posts_failed, Toast.LENGTH_SHORT).show()
            }
        })
    }
}