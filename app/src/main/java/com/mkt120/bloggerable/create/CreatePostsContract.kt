package com.mkt120.bloggerable.create

import android.text.Editable
import android.text.ParcelableSpan
import android.text.Spanned

interface CreatePostsContract {
    interface View {
        fun setBlogTitle(title: String?)
        fun setBlogContent(content: Spanned?)
        fun replaceContent(left: Int, right: Int, text: String)

        fun showConfirmDialog(type: Int)

        fun getSpanLeft(span: ParcelableSpan): Int
        fun getSpanRight(span: ParcelableSpan): Int
        fun addSpan(span: ParcelableSpan, left: Int, right: Int)
        fun removeSpan(span: ParcelableSpan)
        fun setSelection(left: Int, right: Int)

        fun addLabel(label: String)

        fun openBrowser(url: String)

        fun showMessage(textResId: Int)
        fun showMessage(text: String)

        fun showProgress()
        fun dismissProgress()

        fun onComplete(result: Int)
    }

    interface Presenter {
        fun initialize()
        fun onBackPressed(title: String, content: String): Boolean

        fun onClickConfirmPositive(
            isCreatePost: Boolean,
            isDraft: Boolean,
            title: String,
            html: String
        )

        fun onClickAddLabel(label: String)
        fun onClickLabel(label: String)

        fun onClickBold(selectionStart: Int, selectionEnd: Int, text: Editable)
        fun onClickItalic(selectionStart: Int, selectionEnd: Int, text: Editable)
        fun onClickStrikeThrough(selectionStart: Int, selectionEnd: Int, text: Editable)
        fun onClickPaste(selectionLeft: Int, selectionRight: Int, text: String)

        fun onClickUploadAsPosts(title: String, html: String)
        fun onClickUploadAsDraft(title: String, html: String)

        fun onClickUpdatePosts(title: String, html: String)
        fun onClickUpdateDraft(title: String, html: String)

        fun onClickRevertPosts(title: String, html: String)
        fun onClickPublishDraft(title: String, html: String)

        fun onClickDeletePosts()

        fun onClickOpenBlower()
    }
}