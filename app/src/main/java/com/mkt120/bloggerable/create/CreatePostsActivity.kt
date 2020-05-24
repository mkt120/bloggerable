package com.mkt120.bloggerable.create

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.ParcelableSpan
import android.text.Spannable
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import com.mkt120.bloggerable.BaseActivity
import com.mkt120.bloggerable.R
import com.mkt120.bloggerable.datasource.BloggerApiDataSource
import com.mkt120.bloggerable.datasource.PreferenceDataSource
import com.mkt120.bloggerable.datasource.RealmDataSource
import com.mkt120.bloggerable.model.posts.Posts
import com.mkt120.bloggerable.repository.AccountRepository
import com.mkt120.bloggerable.repository.PostsRepository
import com.mkt120.bloggerable.usecase.*
import com.mkt120.bloggerable.util.RealmManager
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlin.math.max
import kotlin.math.min

/**
 * 新規投稿画面 CreatePostsScreen
 */
class CreatePostsActivity : BaseActivity(), Toolbar.OnMenuItemClickListener,
    AddLabelDialogFragment.OnClickListener, CreatePostsContract.View,
    ConfirmDialog.OnClickListener {

    companion object {
        private const val EXTRA_KEY_BLOG_ID = "EXTRA_KEY_BLOG_ID"
        private const val EXTRA_KEY_REQUEST_CODE = "EXTRA_KEY_REQUEST_CODE"
        private const val EXTRA_KEY_POSTS_ID = "EXTRA_KEY_POSTS_ID"
        private const val EXTRA_KEY_LABELS = "EXTRA_KEY_LABELS"
        private const val RELATIVE_FONT_SIZE_X_LARGE = 2.0f
        private const val RELATIVE_FONT_SIZE_LARGE = 1.5f
        private val TAG = CreatePostsActivity::class.java.simpleName

        const val REQUEST_CREATE_POSTS = 100
        const val REQUEST_EDIT_DRAFT = 200
        const val REQUEST_EDIT_POSTS = 300
        const val RESULT_POSTS_UPDATE = 100
        const val RESULT_DRAFT_UPDATE = 200

        fun createIntent(context: Context, blogId: String, labels: ArrayList<String>): Intent =
            Intent(context, CreatePostsActivity::class.java).apply {
                putExtra(
                    EXTRA_KEY_REQUEST_CODE,
                    REQUEST_CREATE_POSTS
                )
                putExtra(EXTRA_KEY_BLOG_ID, blogId)
                putExtra(EXTRA_KEY_LABELS, labels)
            }

        fun createPostsIntent(
            context: Context,
            posts: Posts,
            labels: ArrayList<String>,
            isDraft: Boolean
        ): Intent =
            Intent(context, CreatePostsActivity::class.java).apply {
                if (isDraft) {
                    putExtra(
                        EXTRA_KEY_REQUEST_CODE,
                        REQUEST_EDIT_DRAFT
                    )
                } else {
                    putExtra(
                        EXTRA_KEY_REQUEST_CODE,
                        REQUEST_EDIT_POSTS
                    )
                }
                putExtra(EXTRA_KEY_BLOG_ID, posts.blog!!.id)
                putExtra(EXTRA_KEY_POSTS_ID, posts.id)
                putExtra(EXTRA_KEY_LABELS, labels)
            }
    }

    private var dialogFragment: DialogFragment? = null
    private lateinit var presenter: CreatePostsContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        val requestCode = intent.getIntExtra(EXTRA_KEY_REQUEST_CODE, 0)

        // タイトル
        when (requestCode) {
            REQUEST_CREATE_POSTS -> {
                tool_bar.setTitle(R.string.create_posts_title)
            }
            REQUEST_EDIT_POSTS -> {
                tool_bar.setTitle(R.string.edit_posts_title)
            }
            REQUEST_EDIT_DRAFT -> {
                tool_bar.setTitle(R.string.edit_draft_title)
            }
        }

        // メニュー
        when (requestCode) {
            REQUEST_CREATE_POSTS -> {
                tool_bar.inflateMenu(R.menu.create_posts_menu)
            }
            REQUEST_EDIT_POSTS -> {
                tool_bar.inflateMenu(R.menu.edit_posts_menu)
            }
            REQUEST_EDIT_DRAFT -> {
                tool_bar.inflateMenu(R.menu.edit_draft_menu)
            }
        }
        tool_bar.setOnMenuItemClickListener(this)

        button_add_bold.setOnClickListener {
            presenter.onClickBold(
                edit_text_contents.selectionStart,
                edit_text_contents.selectionEnd,
                edit_text_contents.text
            )
        }
        button_add_italic.setOnClickListener {
            Log.i(TAG, "addItalic")
            presenter.onClickItalic(
                edit_text_contents.selectionStart,
                edit_text_contents.selectionEnd,
                edit_text_contents.text
            )
        }
        button_add_strike_through.setOnClickListener {
            Log.i(TAG, "addStrikeThrough")
            presenter.onClickStrikeThrough(
                edit_text_contents.selectionStart,
                edit_text_contents.selectionEnd,
                edit_text_contents.text
            )
        }
        button_add_paste.setOnClickListener {
            Log.i(TAG, "addPaste")
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            if (clipboard.primaryClip == null) {
                return@setOnClickListener
            }
            val item = clipboard.primaryClip!!.getItemAt(0)
            presenter.onClickPaste(
                edit_text_contents.selectionStart,
                edit_text_contents.selectionEnd,
                item.text.toString()
            )
        }
        button_add_labels.setOnClickListener {
            // todo: 改善の余地あり?
            if (dialogFragment != null && dialogFragment!!.dialog != null && dialogFragment!!.dialog!!.isShowing) {
                return@setOnClickListener
            }
            dialogFragment = AddLabelDialogFragment.newInstance()
            dialogFragment!!.show(supportFragmentManager, null)
        }
        val labels = intent.getStringArrayListExtra(EXTRA_KEY_LABELS)
        button_history.setOnClickListener {
            if (dialogFragment != null && dialogFragment!!.dialog != null && dialogFragment!!.dialog!!.isShowing) {
                return@setOnClickListener
            }
            dialogFragment = AddLabelHistoryDialogFragment.newInstance(labels!!)
            dialogFragment!!.show(supportFragmentManager, null)
        }
        button_add_font_change.setOnClickListener {
            addRelativeSize()
        }

        // draft
        val blogId = intent.getStringExtra(EXTRA_KEY_BLOG_ID)!!
        val postsId = intent.getStringExtra(EXTRA_KEY_POSTS_ID)

        val readBackupFile = ReadBackupFile(cacheDir)
        val createBackupFile = CreateBackupFile(cacheDir)
        val deleteBackupFile = DeleteBackupFile(cacheDir)

        val bloggerApiDataSource = BloggerApiDataSource()
        val realmDataSource = RealmDataSource(RealmManager(getRealm()))
        val postsRepository = PostsRepository(bloggerApiDataSource, realmDataSource)
        val findPosts = FindPosts(postsRepository)
        val preferenceDataSource = PreferenceDataSource()
        val accountRepository = AccountRepository(bloggerApiDataSource, preferenceDataSource)
        val getCurrentUser = GetCurrentAccount(accountRepository)
        val getAccessToken = GetAccessToken(accountRepository)
        val createPosts = CreatePosts(getAccessToken, postsRepository)
        val updatePosts = UpdatePosts(getAccessToken, postsRepository)
        val revertPosts = RevertPosts(getAccessToken, postsRepository)
        val publishPost = PublishPosts(getAccessToken, postsRepository)
        val deletePosts = DeletePosts(getAccessToken, postsRepository)
        presenter = CreatePostsPresenter(
            this@CreatePostsActivity,
            getCurrentUser,
            blogId,
            postsId,
            readBackupFile,
            createBackupFile,
            deleteBackupFile,
            findPosts,
            createPosts,
            updatePosts,
            revertPosts,
            publishPost,
            deletePosts,
            requestCode
        )
        presenter.initialize()

    }

    override fun setBlogTitle(title: String?) {
        edit_text_title.setText(title)
    }

    override fun setBlogContent(content: Spanned?) {
        edit_text_contents.setText(content)
    }

    override fun onClickAddLabel(label: String) {
        presenter.onClickAddLabel(label)
    }

    override fun addLabel(label: String) {
        val view = LabelView(this@CreatePostsActivity).apply {
            text = label
            setOnClickListener {
                label_view.removeView(this)
                presenter.onClickLabel(label)
            }
        }
        label_view.addView(view)
        horizontal_scroll.post {
            horizontal_scroll.scrollTo(view.right, 0)
        }
    }

    override fun showProgress() {
        progress_view.visibility = View.VISIBLE
    }

    override fun dismissProgress() {
        progress_view.visibility = View.GONE
    }

    override fun showMessage(textResId: Int) {
        showMessage(applicationContext.getString(textResId))
    }

    override fun showMessage(text: String) {
        Toast.makeText(
            this@CreatePostsActivity,
            text,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun openBrowser(url: String) {
        val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(i)
    }

    override fun onComplete(result: Int) {
        setResult(result)
        finish()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        val title = edit_text_title.text.toString()
        val content = HtmlCompat.toHtml(
            edit_text_contents.text,
            HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE
        )
        when (item!!.itemId) {
            R.id.open_in_browser -> {
                presenter.onClickOpenBlower()
                return true
            }
            R.id.create_posts -> {
                presenter.onClickUploadAsPosts(title, content)
                return true
            }
            R.id.update_posts -> {
                presenter.onClickUpdatePosts(title, content)
                return true
            }
            R.id.update_draft -> {
                presenter.onClickUpdateDraft(title, content)
                return true
            }
            R.id.publish_draft -> {
                presenter.onClickPublishDraft(title, content)
                return true
            }
            R.id.revert_posts -> {
                presenter.onClickRevertPosts(title, content)
                return true
            }
            R.id.upload_as_draft -> {
                presenter.onClickUploadAsDraft(title, content)
                return true
            }
            R.id.delete_posts -> {
                val dialog = ConfirmDialog.newInstance(CreatePostsContract.TYPE.DELETE_POST)
                dialog.show(supportFragmentManager, null)
                return true
            }
        }
        return false
    }

    override fun onBackPressed() {
        val title = edit_text_title.text.toString()
        val html = HtmlCompat.toHtml(
            edit_text_contents.text,
            HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE
        )
        if (presenter.onBackPressed(title, html)) {
            return
        }
        super.onBackPressed()
    }

    // region *** handle confirmDialog ***
    override fun showConfirmDialog(type: CreatePostsContract.TYPE) {
        val confirmDialog: ConfirmDialog = ConfirmDialog.newInstance(type)
        confirmDialog.show(supportFragmentManager, null)
    }

    override fun onConfirmPositiveClick(type: CreatePostsContract.TYPE) {
        val title = edit_text_title.text.toString()
        val html = HtmlCompat.toHtml(
            edit_text_contents.text,
            HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE
        )
        presenter.onClickConfirmPositive(type, title, html)
    }

    override fun onConfirmNegativeClick(type: CreatePostsContract.TYPE) {
        presenter.onClickConfirmNegative(type)
    }

    override fun onConfirmNeutralClick(type: CreatePostsContract.TYPE) {
        val title = edit_text_title.text.toString()
        val html = HtmlCompat.toHtml(
            edit_text_contents.text,
            HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE
        )
        presenter.onConfirmNeutralButton(type, title, html)
    }
    // endregion

    // region *** edit Content ***
    override fun getSpanLeft(span: ParcelableSpan): Int = min(
        edit_text_contents.text.getSpanStart(span),
        edit_text_contents.text.getSpanEnd(span)
    )

    override fun getSpanRight(span: ParcelableSpan): Int = max(
        edit_text_contents.text.getSpanStart(span),
        edit_text_contents.text.getSpanEnd(span)
    )

    override fun removeSpan(span: ParcelableSpan) {
        edit_text_contents.text.removeSpan(span)
    }

    override fun replaceContent(left: Int, right: Int, text: String) {
        edit_text_contents.text.replace(left, right, text)
        edit_text_contents.setSelection(left + text.length)
    }

    override fun setSelection(left: Int, right: Int) {
        edit_text_contents.setSelection(left, right)
    }

    /**
     * spanを追加する
     */
    override fun addSpan(span: ParcelableSpan, left: Int, right: Int) {
        Log.i("CreatePostsActivity", "addSpan left=$left, right=$right, span=$span")
        val spannable: Spannable = edit_text_contents.text
        spannable.setSpan(span, left, right, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        edit_text_contents.setText(spannable)
    }

    /**
     * 文字サイズを変更する TODO:要改善
     */
    private fun addRelativeSize() {
        Log.i(TAG, "addRelativeSize")
        val cursorLeft = min(edit_text_contents.selectionStart, edit_text_contents.selectionEnd)
        val cursorRight = max(edit_text_contents.selectionStart, edit_text_contents.selectionEnd)
        val spans =
            edit_text_contents.text.getSpans(cursorLeft, cursorRight, RelativeSizeSpan::class.java)
        if (spans.isEmpty()) {
            addSpan(RelativeSizeSpan(RELATIVE_FONT_SIZE_LARGE), cursorLeft, cursorRight)
            setSelection(cursorLeft, cursorRight)
            return
        }
        if (spans.size == 1) {
            val spanLeft = getSpanLeft(spans[0])
            val spanRight = getSpanRight(spans[0])
            if (spanLeft == cursorLeft && spanRight == cursorRight) {
                // 完全に一致
                removeSpan(spans[0])
                if (spans[0].sizeChange == RELATIVE_FONT_SIZE_LARGE) {
                    addSpan(RelativeSizeSpan(RELATIVE_FONT_SIZE_X_LARGE), cursorLeft, cursorRight)
                }
                setSelection(cursorLeft, cursorRight)
                return
            }
        }

        // それ以外は一番大きいサイズに合わせてspanを付与
        for (span in spans) {
            val spanLeft = getSpanLeft(span)
            val spanRight = getSpanRight(span)
            removeSpan(span)

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
        setSelection(cursorLeft, cursorRight)
    }

    // endregion editContent

}