package com.mkt120.bloggerable

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_posts_detail.*

class PostsDetailActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_KEY_BLOG_ID = "EXTRA_KEY_BLOG_ID"
        private const val EXTRA_KEY_TITLE = "EXTRA_KEY_TITLE"
        private const val EXTRA_KEY_CONTENT = "EXTRA_KEY_CONTENT"

        fun createIntent(context: Context, blogId: String, title: String, content: String): Intent =
            Intent(context, PostsDetailActivity::class.java).apply {
                putExtra(EXTRA_KEY_BLOG_ID, blogId)
                putExtra(EXTRA_KEY_TITLE, title)
                putExtra(EXTRA_KEY_CONTENT, content)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts_detail)
        val title = intent.getStringExtra(EXTRA_KEY_TITLE)
        title_view.text = title

        val content = intent.getStringExtra(EXTRA_KEY_CONTENT)
        val b64Encode: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            java.util.Base64.getEncoder().encodeToString(content.toByteArray())
        } else {
            Base64.encode(content.toByteArray(), Base64.DEFAULT).toString()
        }
        contents_view.loadData(b64Encode, "text/html", "base64")

    }
}