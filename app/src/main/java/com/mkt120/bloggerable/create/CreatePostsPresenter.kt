package com.mkt120.bloggerable.create

import android.graphics.Typeface
import android.text.Editable
import android.text.Html
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.util.Log
import com.mkt120.bloggerable.R
import com.mkt120.bloggerable.model.Account
import com.mkt120.bloggerable.model.posts.Posts
import com.mkt120.bloggerable.usecase.*
import kotlin.math.max
import kotlin.math.min

class CreatePostsPresenter(
    private val view: CreatePostsContract.View,
    private val getCurrentAccount: GetCurrentAccount,
    private val blogId: String,
    private val postsId: String? = null,
    private val readBackupFile: ReadBackupFile,
    private val createBackupFile: CreateBackupFile,
    private val deleteBackupFile: DeleteBackupFile,
    private val findPosts: FindPosts,
    private val createPost: CreatePosts,
    private val updatePost: UpdatePosts,
    private val revertPosts: RevertPosts,
    private val publishPosts: PublishPosts,
    private val deletePosts: DeletePosts,
    private val requestCode: Int
) :
    CreatePostsContract.Presenter {

    private var currentAccount: Account = getCurrentAccount.execute()!!
    private val labelList = mutableListOf<String>()
    private var isExecuting = false
    private var posts: Posts? = null
    private var backupPost: Posts? = null

    init {
        postsId?.let {
            try {
                posts = findPosts.execute(blogId, postsId).blockingGet()
            } catch (e: Exception) {
            }
        }
        backupPost = readBackupFile.execute(blogId, postsId)
    }

    override fun initialize() {
        posts?.let {
            view.setBlogTitle(it.title)
            view.setBlogContent(Html.fromHtml(it.content, Html.FROM_HTML_MODE_COMPACT))
            it.labels?.let { labels ->
                for (label in labels) {
                    addLabel(label)
                }
            }
        }
        backupPost?.let {
            Log.i("CreatePostsPresenter", "backupPost title=${it.title}, html=${it.content}")
            view.showConfirmDialog(CreatePostsContract.TYPE.EXIST_BACK_UP)
        }
    }

    override fun onClickOpenBlower() {
        view.openBrowser(posts!!.url!!)
    }

    override fun onBackPressed(title: String, html: String): Boolean {
        if (isExecuting) {
            return true
        }
        val isEmptyContent = (title.isEmpty() && html.isEmpty())
        if (postsId == null) {
            // 新規
            return if (!isEmptyContent) {
                // 何か書いてる 下書きとしてアップロード
                createPosts(true, title, html, labelList.toTypedArray())
                true
            } else {
                // 何も書いてない
                false
            }
        }

        if (posts != null && posts!!.isChange(title, html)) {
            // 変更あり
            val type: CreatePostsContract.TYPE =
                if (requestCode == CreatePostsActivity.REQUEST_EDIT_POSTS) {
                    CreatePostsContract.TYPE.BACK_EDIT_POSTS
                } else {
                    CreatePostsContract.TYPE.BACK_EDIT_DRAFT
                }
            view.showConfirmDialog(type)
            return true
        }
        return false
    }

    // region *** handle confirm dialog ***

    override fun onClickConfirmPositive(
        type: CreatePostsContract.TYPE,
        title: String,
        html: String
    ) {

        when (type) {
            CreatePostsContract.TYPE.EXIST_BACK_UP -> {
                backupPost?.let {
                    Log.i(
                        "CreatePostsPresenter",
                        "backupPost title=${it.title}, html=${it.content}"
                    )
                    view.setBlogTitle(it.title)
                    view.setBlogContent(Html.fromHtml(it.content, Html.FROM_HTML_MODE_COMPACT))
                }
            }
            CreatePostsContract.TYPE.DELETE_POST -> {
                val isDraft = requestCode == CreatePostsActivity.REQUEST_EDIT_DRAFT
                deletePosts(currentAccount.getId(), isDraft)

                // ファイルを削除する
                deleteBackupFile.execute(blogId, postsId)
            }
            else -> {
                updatePosts(
                    currentAccount.getId(),
                    isDraft = type.isDraft(),
                    isPublish = false,
                    isRevert = false,
                    title = title,
                    html = html,
                    labels = labelList.toTypedArray()
                )
            }
        }
    }

    override fun onClickConfirmNegative(type: CreatePostsContract.TYPE) {
        when (type) {
            CreatePostsContract.TYPE.EXIST_BACK_UP -> {
                // ファイルを削除する
                deleteBackupFile.execute(blogId, postsId)
            }
            CreatePostsContract.TYPE.DELETE_POST -> {
                // 何もしない
            }
            else -> {
                // 終了する
                deleteBackupFile.execute(blogId, postsId)
                val isDraft = requestCode == CreatePostsActivity.REQUEST_EDIT_DRAFT
                val result = if (isDraft) {
                    CreatePostsActivity.RESULT_DRAFT_UPDATE
                } else {
                    CreatePostsActivity.RESULT_POSTS_UPDATE
                }
                view.onComplete(result)
            }
        }
    }

    override fun onConfirmNeutralButton(
        type: CreatePostsContract.TYPE,
        title: String, html: String
    ) {
        createBackupFile.execute(blogId, postsId, title, html)
        if (type.isDraft()) {
            view.onComplete(CreatePostsActivity.RESULT_DRAFT_UPDATE)
        } else {
            view.onComplete(CreatePostsActivity.RESULT_POSTS_UPDATE)
        }
    }
    // endregion

    // region *** handle content ***
    override fun onClickPaste(selectionLeft: Int, selectionRight: Int, text: String) {
        val left = min(selectionLeft, selectionRight)
        val right = max(selectionLeft, selectionRight)
        view.replaceContent(left, right, text)
    }

    /**
     * Italicを追加・削除する
     */
    override fun onClickItalic(selectionStart: Int, selectionEnd: Int, text: Editable) {
        val selectionLeft = min(selectionStart, selectionEnd)
        val selectionRight = max(selectionStart, selectionEnd)
        val spans = text.getSpans(selectionLeft, selectionRight, StyleSpan::class.java)
        val hasItalic = spans.isNotEmpty() && spans.any { (it.style != Typeface.BOLD) }

        if (spans.isEmpty() || !hasItalic) {
            // italicをつけるだけ
            view.addSpan(StyleSpan(Typeface.ITALIC), selectionLeft, selectionRight)
            view.setSelection(selectionLeft, selectionRight)
            return
        }

        // italic外すだけ
        for (span in spans) {
            if (span.style == Typeface.BOLD) {
                continue
            }
            val spanLeft = view.getSpanLeft(span)
            val spanRight = view.getSpanRight(span)
            view.removeSpan(span)

            if (spanLeft < selectionLeft || selectionRight < spanRight) {
                // 選択範囲が1つのspanに含まれている
                if (spanLeft < selectionLeft) {
                    // 左が出てる 左にspanつけなおし
                    val clone = StyleSpan(span.style)
                    view.addSpan(clone, spanLeft, selectionLeft)
                }
                if (selectionRight < spanRight) {
                    // 右が出てる 右にspanつけなおし
                    val clone = StyleSpan(span.style)
                    view.addSpan(clone, selectionRight, spanRight)
                }
            }
        }
        view.setSelection(selectionLeft, selectionRight)
    }

    /**
     * boldを追加・削除する
     */
    override fun onClickBold(selectionStart: Int, selectionEnd: Int, text: Editable) {
        val selectionLeft = min(selectionStart, selectionEnd)
        val selectionRight = max(selectionStart, selectionEnd)
        val spans = text.getSpans(selectionLeft, selectionRight, StyleSpan::class.java)
        val hasBold = spans.isNotEmpty() && spans.any { (it.style != Typeface.ITALIC) }
        if (spans.isEmpty() || !hasBold) {
            // boldをつけるだけ
            view.addSpan(StyleSpan(Typeface.BOLD), selectionLeft, selectionRight)
            view.setSelection(selectionLeft, selectionRight)
            return
        }

        // boldを外すだけ
        for (span in spans) {
            if (span.style == Typeface.ITALIC) {
                continue
            }
            val spanLeft = view.getSpanLeft(span)
            val spanRight = view.getSpanRight(span)
            view.removeSpan(span)

            if (spanLeft < selectionLeft || selectionRight < spanRight) {
                // 選択範囲が1つのspanに含まれている
                var clone = StyleSpan(span.style)
                if (spanLeft < selectionLeft) {
                    // 左が出てる 左にspanつけなおし
                    view.addSpan(clone, spanLeft, selectionLeft)
                    clone = StyleSpan(span.style)
                }
                if (selectionRight < spanRight) {
                    // 右が出てる 右にspanつけなおし
                    view.addSpan(clone, selectionRight, spanRight)
                }
            }
        }
        view.setSelection(selectionLeft, selectionRight)
    }

    /**
     * 打消し線を追加・削除する
     */
    override fun onClickStrikeThrough(selectionStart: Int, selectionEnd: Int, text: Editable) {
        val selectionLeft = min(selectionStart, selectionEnd)
        val selectionRight = max(selectionStart, selectionEnd)
        val spans = text.getSpans(selectionLeft, selectionRight, StrikethroughSpan::class.java)
        if (spans.isEmpty()) {
            view.addSpan(StrikethroughSpan(), selectionLeft, selectionRight)
            view.setSelection(selectionLeft, selectionRight)
            return
        }

        for (span in spans) {
            val spanLeft = view.getSpanLeft(span)
            val spanRight = view.getSpanRight(span)
            view.removeSpan(span)

            if (spanLeft < selectionLeft || selectionRight < spanRight) {
                // 選択範囲が1つのspanに含まれている
                if (spanLeft < selectionLeft) {
                    // 左が出てる 左にspanつけなおし
                    val clone = StrikethroughSpan()
                    view.addSpan(clone, spanLeft, selectionLeft)
                }
                if (selectionRight < spanRight) {
                    val clone = StrikethroughSpan()
                    // 右が出てる 右にspanつけなおし
                    view.addSpan(clone, selectionRight, spanRight)
                }
            }
        }
        view.setSelection(selectionLeft, selectionRight)
    }

    // endregion

    // region *** handle label ***

    override fun onClickAddLabel(label: String) {
        addLabel(label)
    }

    private fun addLabel(label: String) {
        if (labelList.contains(label)) {
            view.showMessage(R.string.toast_error_same_label_already_inserted)
            return
        }
        if (label.isEmpty()) {
            // 空のラベルは入れられない
            return
        }
        labelList.add(label)
        view.addLabel(label)
    }

    override fun onClickLabel(label: String) {
        labelList.remove(label)
    }

    // endregion

    // region *** handle post ***
    override fun onClickPublishDraft(title: String, html: String) {
        updatePosts(
            currentAccount.getId(),
            isDraft = true,
            isPublish = true,
            isRevert = false,
            title = title,
            html = html,
            labels = labelList.toTypedArray()
        )
    }

    override fun onClickUpdateDraft(
        title: String,
        html: String
    ) {
        updatePosts(
            currentAccount.getId(),
            requestCode == CreatePostsActivity.REQUEST_EDIT_DRAFT,
            isPublish = false,
            isRevert = false,
            title = title,
            html = html,
            labels = labelList.toTypedArray()
        )

    }

    /**
     * 記事として投稿する
     */
    override fun onClickUploadAsPosts(
        title: String,
        html: String
    ) {
        if (requestCode == CreatePostsActivity.REQUEST_CREATE_POSTS) {
            createPosts(false, title, html, labelList.toTypedArray())
        } else {
            updatePosts(
                currentAccount.getId(),
                requestCode == CreatePostsActivity.REQUEST_EDIT_DRAFT,
                isPublish = false,
                isRevert = false,
                title = title,
                html = html,
                labels = labelList.toTypedArray()
            )
        }
    }

    /**
     * 下書きとして投稿する
     */
    override fun onClickUploadAsDraft(
        title: String,
        html: String
    ) {
        createPosts(true, title, html, labelList.toTypedArray())
    }

    /**
     * 記事を更新する
     */
    override fun onClickUpdatePosts(
        title: String,
        html: String
    ) {
        updatePosts(
            currentAccount.getId(),
            requestCode == CreatePostsActivity.REQUEST_EDIT_DRAFT,
            isPublish = false,
            isRevert = false,
            title = title,
            html = html,
            labels = labelList.toTypedArray()
        )
    }

    /**
     * 下書きに戻す
     */
    override fun onClickRevertPosts(
        title: String,
        html: String
    ) {
        updatePosts(
            currentAccount.getId(),
            isDraft = false,
            isPublish = false,
            isRevert = true,
            title = title,
            html = html,
            labels = labelList.toTypedArray()
        )
    }

    /**
     * 記事の削除する
     */
    override fun onClickDeletePosts() {
        val isDraft = requestCode == CreatePostsActivity.REQUEST_EDIT_DRAFT
        deletePosts(currentAccount.getId(), isDraft)
    }

    /**
     * 新規に投稿する
     */
    private fun createPosts(
        isDraft: Boolean,
        title: String,
        html: String,
        labels: Array<String>?
    ) {
        if (!isDraft && title.isEmpty()) {
            // 空 empty title
            view.showMessage(R.string.toast_error_create_posts_no_title)
            return
        }
        isExecuting = true
        view.showProgress()
        createPost.execute(
            currentAccount.getId(),
            blogId,
            title,
            html,
            labels,
            isDraft
        ).subscribe({
            isExecuting = false
            val messageResId: Int = if (isDraft) {
                R.string.toast_create_draft_success
            } else {
                R.string.toast_create_posts_success
            }
            view.showMessage(messageResId)
            val result = if (isDraft) {
                CreatePostsActivity.RESULT_DRAFT_UPDATE
            } else {
                CreatePostsActivity.RESULT_POSTS_UPDATE
            }
            view.onComplete(result)
        }, {
            isExecuting = false
            view.showMessage(R.string.toast_create_posts_failed)
        })

    }

    /**
     * 投稿を更新する
     */
    private fun updatePosts(
        userId: String,
        isDraft: Boolean,
        isPublish: Boolean = false,
        isRevert: Boolean = false,
        title: String,
        html: String,
        labels: Array<String>?
    ) {
        if (title.isEmpty()) {
            // 空 empty title
            view.showMessage(R.string.toast_error_create_posts_no_title)
            return
        }
        isExecuting = true
        view.showProgress()
        updatePost.execute(System.currentTimeMillis(), userId, posts!!, title, html, labels)
            .subscribe({
                isExecuting = false
                when {
                    isPublish -> publishPosts(userId, posts!!)
                    isRevert -> revertPosts(userId, posts!!.blog!!.id!!, posts!!.id!!)
                    else -> {
                        view.showMessage(R.string.toast_success_update_posts)
                        val result = if (isDraft) {
                            CreatePostsActivity.RESULT_DRAFT_UPDATE
                        } else {
                            CreatePostsActivity.RESULT_POSTS_UPDATE
                        }
                        view.onComplete(result)
                    }
                }
            }, {
                isExecuting = false
                view.showMessage(R.string.toast_create_posts_failed)
            })
    }

    /**
     * 下書きに戻す
     */
    private fun revertPosts(userId: String, blogId: String, postsId: String) {
        isExecuting = true
        view.showProgress()
        revertPosts.execute(System.currentTimeMillis(), userId, blogId, postsId).subscribe(
            {
                isExecuting = false
                view.showMessage("投稿を下書きに戻しました")
                view.onComplete(CreatePostsActivity.RESULT_DRAFT_UPDATE)
            }, {
                isExecuting = false
                //todo:エラー
                view.showMessage("エラー")
            })
    }

    /**
     * 下書きを公開する
     */
    private fun publishPosts(userId: String, posts: Posts) {
        view.showProgress()
        publishPosts.execute(System.currentTimeMillis(), userId, blogId, posts.id!!).subscribe({
            isExecuting = false
            view.showMessage(R.string.toast_publish_posts_success)
            view.onComplete(CreatePostsActivity.RESULT_POSTS_UPDATE)
        }, {
            isExecuting = false
            view.showMessage(R.string.toast_create_posts_failed)
        })
    }

    /**
     * 投稿を削除する
     */
    private fun deletePosts(userId: String, isDraft: Boolean) {
        view.showProgress()
        isExecuting = true
        deletePosts.execute(userId, blogId, posts!!.id!!).subscribe({
            isExecuting = false
            view.showMessage(R.string.toast_success_delete_posts)
            val result = if (isDraft) {
                CreatePostsActivity.RESULT_DRAFT_UPDATE
            } else {
                CreatePostsActivity.RESULT_POSTS_UPDATE
            }
            view.onComplete(result)
        }, {
            isExecuting = false
            view.showMessage(R.string.toast_failed_delete_posts)
        })
    }

    // endregion
}