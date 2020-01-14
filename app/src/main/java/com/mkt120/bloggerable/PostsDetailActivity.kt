package com.mkt120.bloggerable

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.mkt120.bloggerable.model.Posts
import kotlinx.android.synthetic.main.activity_posts_detail.*

class PostsDetailActivity : AppCompatActivity(), Toolbar.OnMenuItemClickListener {

    companion object {
        private const val EXTRA_KEY_BLOG_ID = "EXTRA_KEY_BLOG_ID"
        private const val EXTRA_KEY_POSTS_ID = "EXTRA_KEY_POSTS_ID"
        private const val EXTRA_KEY_POSTS = "EXTRA_KEY_POSTS"

        fun createIntent(
            context: Context,
            blogId:String,
            posts:Posts
        ): Intent =
            Intent(context, PostsDetailActivity::class.java).apply {
                putExtra(EXTRA_KEY_BLOG_ID, blogId)
                putExtra(EXTRA_KEY_POSTS_ID, posts.id)
                putExtra(EXTRA_KEY_POSTS, posts)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts_detail)

        tool_bar.setTitle(R.string.posts_detail_title)
        tool_bar.inflateMenu(R.menu.posts_detail_menu)
        tool_bar.setOnMenuItemClickListener(this)

        val posts: Posts = intent.getParcelableExtra(EXTRA_KEY_POSTS)!!
        title_view.text = posts.title

        val content = posts.content!!
        val b64Encode: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            java.util.Base64.getEncoder().encodeToString(content.toByteArray())
        } else {
            Base64.encode(content.toByteArray(), Base64.DEFAULT).toString()
        }
        contents_view.loadData(b64Encode, "text/html", "base64")
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.open_in_browser -> {
                val posts: Posts = intent.getParcelableExtra(EXTRA_KEY_POSTS)!!
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(posts.url))
                startActivity(i)
                return true
            }
            R.id.delete_posts -> {
                // 削除ボタン
                val postsId: String = intent.getStringExtra(EXTRA_KEY_POSTS_ID)!!
                val blogId: String = intent.getStringExtra(EXTRA_KEY_BLOG_ID)!!
                deletePosts(blogId, postsId)
                return true
            }
            R.id.edit_posts -> {
                return false
            }
        }
        return false
    }

    private fun deletePosts(blogId: String, postsId: String) {
        ApiManager.deletePosts(
            blogId,
            postsId,
            object : ApiManager.CompleteListener {
                override fun onComplete() {
                    Toast.makeText(this@PostsDetailActivity, R.string.toast_detail_posts_success_delete, Toast.LENGTH_SHORT).show()
                    finish()
                }
                override fun onFailed(t: Throwable) {
                    Toast.makeText(this@PostsDetailActivity, R.string.toast_detail_posts_failed_delete, Toast.LENGTH_SHORT).show()
                }
            })
    }
}