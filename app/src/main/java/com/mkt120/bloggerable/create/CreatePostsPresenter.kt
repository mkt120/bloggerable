package com.mkt120.bloggerable.create

import android.graphics.Typeface
import android.text.Editable
import android.text.Html
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import com.mkt120.bloggerable.ApiManager
import com.mkt120.bloggerable.R
import com.mkt120.bloggerable.model.Posts
import kotlin.math.max
import kotlin.math.min

class CreatePostsPresenter(
    private val view: CreatePostsContract.View,
    private val blogId: String,
    private val posts: Posts?,
    private val requestCode: Int
) :
    CreatePostsContract.Presenter {

    private val labelList = mutableListOf<String>()
    private var isExecuting = false

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

    override fun onClickConfirmPositive(
        isCreatePost: Boolean,
        isDraft: Boolean,
        title: String,
        content: Editable
    ) {
        if (isCreatePost) {
            createPosts(true, title, content, labelList.toTypedArray())
        } else {
            updatePosts(
                isDraft,
                isPublish = false,
                isRevert = false,
                title = title,
                content = content,
                labels = labelList.toTypedArray()
            )
        }
    }

    override fun onClickOpenBlower() {
        view.openBrowser(posts!!.url!!)
    }

    override fun onClickPaste(selectionLeft: Int, selectionRight: Int, text: String) {
        val left = min(selectionLeft, selectionRight)
        val right = max(selectionLeft, selectionRight)
        view.replaceContent(left, right, text)
    }

    override fun onClickPublishDraft(title: String, content: Editable) {
        updatePosts(
            isDraft = true,
            isPublish = true,
            isRevert = false,
            title = title,
            content = content,
            labels = labelList.toTypedArray()
        )
    }

    override fun onClickUpdateDraft(
        title: String,
        content: Editable
    ) {
        updatePosts(
            requestCode == CreatePostsActivity.REQUEST_EDIT_DRAFT,
            isPublish = false,
            isRevert = false,
            title = title,
            content = content,
            labels = labelList.toTypedArray()
        )

    }

    override fun onCreate() {
        posts?.let {
            view.setBlogTitle(it.title)
            view.setBlogContent(Html.fromHtml(it.content, Html.FROM_HTML_MODE_COMPACT))
            it.labels?.let { labels ->
                for (label in labels) {
                    addLabel(label)
                }
            }
        }
    }

    override fun onClickAddLabel(label: String) {
        addLabel(label)
    }

    private fun addLabel(label: String) {
        if (labelList.contains(label)) {
            view.showToast("already inserted.")
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

    override fun onBackPressed(title: String, content: String): Boolean {
        if (isExecuting) {
            return true
        }
        if (posts == null) {
            if (title.isNotEmpty() || content.isNotEmpty()) {
                view.showConfirmDialog(ConfirmDialog.TYPE_CREATE)
                return true
            }
        } else if (posts.isChange(title, content)) {
            val type = if (requestCode == CreatePostsActivity.REQUEST_EDIT_POSTS) {
                ConfirmDialog.TYPE_EDIT_POSTS
            } else {
                ConfirmDialog.TYPE_EDIT_DRAFT
            }
            view.showConfirmDialog(type)
            return true
        }
        return false
    }

    override fun onClickUploadAsPosts(
        title: String,
        content: Editable
    ) {
        if (requestCode == CreatePostsActivity.REQUEST_CREATE_POSTS) {
            createPosts(false, title, content, labelList.toTypedArray())
        } else {
            updatePosts(
                requestCode == CreatePostsActivity.REQUEST_EDIT_DRAFT,
                isPublish = false,
                isRevert = false,
                title = title,
                content = content,
                labels = labelList.toTypedArray()
            )
        }
    }

    override fun onClickUpdatePosts(
        title: String,
        content: Editable
    ) {
        updatePosts(
            requestCode == CreatePostsActivity.REQUEST_EDIT_DRAFT,
            isPublish = false,
            isRevert = false,
            title = title,
            content = content,
            labels = labelList.toTypedArray()
        )
    }

    override fun onClickUploadAsDraft(
        title: String,
        content: Editable
    ) {
        createPosts(true, title, content, labelList.toTypedArray())
    }

    override fun onClickRevertPosts(
        title: String,
        content: Editable
    ) {
        updatePosts(
            isDraft = false,
            isPublish = false,
            isRevert = true,
            title = title,
            content = content,
            labels = labelList.toTypedArray()
        )
    }

    /**
     * 新規に投稿する
     */
    private fun createPosts(
        isDraft: Boolean,
        title: String,
        content: Editable,
        labels: Array<String>?
    ) {
        if (!isDraft && title.isEmpty()) {
            // 空 empty title
            view.showToast(R.string.toast_error_create_posts_no_title)
            return
        }

        isExecuting = true
        view.showProgress()
        val html = Html.toHtml(content, Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL)
        ApiManager.createPosts(
            blogId,
            title,
            html,
            labels,
            isDraft,
            object : ApiManager.CompleteListener {
                override fun onComplete() {
                    isExecuting = false
                    val messageResId: Int = if (isDraft) {
                        R.string.toast_create_draft_success
                    } else {
                        R.string.toast_create_posts_success
                    }
                    view.showToast(messageResId)
                    val result = if (isDraft) {
                        (CreatePostsActivity.RESULT_DRAFT_UPDATE)
                    } else {
                        (CreatePostsActivity.RESULT_POSTS_UPDATE)
                    }
                    view.onComplete(result)
                }

                override fun onFailed(t: Throwable) {
                    isExecuting = false
                    view.showToast(R.string.toast_create_posts_failed)
                }
            })
    }

    /**
     * 投稿を更新する
     */
    private fun updatePosts(
        isDraft: Boolean,
        isPublish: Boolean = false,
        isRevert: Boolean = false,
        title: String,
        content: Editable,
        labels: Array<String>?
    ) {
        if (title.isEmpty()) {
            // 空 empty title
            view.showToast(R.string.toast_error_create_posts_no_title)
            return
        }
        isExecuting = true
        view.showProgress()
        val html = Html.toHtml(content, Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL)
        posts!!.title = title
        posts.content = html
        posts.labels = labels
        ApiManager.updatePosts(
            posts,
            object : ApiManager.CompleteListener {
                override fun onComplete() {
                    isExecuting = false
                    when {
                        isPublish -> publishPosts(posts)
                        isRevert -> revertPosts(posts.blog!!.id!!, posts.id!!)
                        else -> {
                            view.showToast(R.string.toast_success_update_posts)
                            val result = if (isDraft) {
                                (CreatePostsActivity.RESULT_DRAFT_UPDATE)
                            } else {
                                (CreatePostsActivity.RESULT_POSTS_UPDATE)
                            }
                            view.onComplete(result)
                        }
                    }
                }

                override fun onFailed(t: Throwable) {
                    isExecuting = false
                }
            })
    }

    private fun revertPosts(blogId: String, postsId: String) {
        isExecuting = true
        view.showProgress()
        ApiManager.revertPosts(
            blogId,
            postsId,
            object : ApiManager.CompleteListener {
                override fun onComplete() {
                    isExecuting = false
                    view.showToast("投稿を下書きに戻しました")
                    view.onComplete(CreatePostsActivity.RESULT_DRAFT_UPDATE)
                }

                override fun onFailed(t: Throwable) {
                }
            })
    }

    private fun publishPosts(posts: Posts) {
        view.showProgress()
        ApiManager.publishPosts(
            posts.blog!!.id!!,
            posts.id!!,
            object : ApiManager.CompleteListener {
                override fun onComplete() {
                    view.showToast("投稿を公開しました")
                    view.onComplete(CreatePostsActivity.RESULT_POSTS_UPDATE)
                }

                override fun onFailed(t: Throwable) {
                    // todo:onFailed
                }
            })
    }

    override fun onClickDeletePosts() {
        val isDraft = requestCode == CreatePostsActivity.REQUEST_EDIT_DRAFT
        deletePosts(isDraft)
    }

    /**
     * 投稿を削除する
     */
    private fun deletePosts(isDraft: Boolean) {
        view.showProgress()
        isExecuting = true
        ApiManager.deletePosts(
            blogId,
            posts!!.id!!,
            object : ApiManager.CompleteListener {
                override fun onComplete() {
                    isExecuting = false
                    view.showToast(R.string.toast_success_delete_posts)
                    val result = if (isDraft) {
                        (CreatePostsActivity.RESULT_DRAFT_UPDATE)
                    } else {
                        (CreatePostsActivity.RESULT_POSTS_UPDATE)
                    }
                    view.onComplete(result)
                }

                override fun onFailed(t: Throwable) {
                    isExecuting = false
                    view.showToast(R.string.toast_failed_delete_posts)
                }
            })
    }
}