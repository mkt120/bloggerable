package com.mkt120.bloggerable

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataSet
import kotlinx.android.synthetic.main.activity_create_post.*

class CreatePostsActivity : AppCompatActivity() {
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

        tool_bar.title = "新規投稿"

        button_create.setOnClickListener {
            createPosts()
        }
    }

    private fun createPosts() {
        val blogId = intent.getStringExtra(EXTRA_KEY_BLOG_ID)!!
        val title = edit_text_title.text.toString()
        val content = edit_text_contents.text.toString()

        if (title.isEmpty()) {
            // 空 empty title
            Toast.makeText(this@CreatePostsActivity, "タイトルがありません", Toast.LENGTH_SHORT).show()
            return
        }

        // 改行文字をHTMLに差し替え
        val options = MutableDataSet()
        options.set(
            Parser.EXTENSIONS,
            listOf(TablesExtension.create(), StrikethroughExtension.create())
        )

        // Markdown to HTML
        val parser = Parser.builder(options).build()
        val renderer = HtmlRenderer.builder(options).build()
        val document = parser.parse(content)
        val html = renderer.render(document)

        // create Markdown string
        // val markdown = FlexmarkHtmlConverter.builder(options).build().convert(html)

        ApiManager.createPosts(blogId, title, html, object : ApiManager.CompleteListener {
            override fun onComplete() {
                Toast.makeText(this@CreatePostsActivity, "成功しました", Toast.LENGTH_SHORT).show()
                finish()
            }

            override fun onFailed(t: Throwable) {
                Toast.makeText(this@CreatePostsActivity, "失敗しました", Toast.LENGTH_SHORT).show()
            }
        })
    }
}