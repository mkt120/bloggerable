package com.mkt120.bloggerable

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.SpannableStringBuilder
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

        button_create.setOnClickListener {
            createPosts()
        }
    }

    private fun createPosts() {
        val blogId = intent.getStringExtra(EXTRA_KEY_BLOG_ID)!!
        val title = edit_text_title.text.toString()
        val contentTemporary = edit_text_contents.text.toString()

        if (title.isEmpty()) {
            // 空 empty title
            Toast.makeText(this@CreatePostsActivity, "タイトルがありません", Toast.LENGTH_SHORT).show()
            return
        }

        val span = SpannableStringBuilder(contentTemporary)
        val content = Html.toHtml(span, 0)

        ApiManager.createPosts(this@CreatePostsActivity, blogId, title, content, object : ApiManager.CompleteListener {
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