package com.mkt120.bloggerable.create

import android.text.Editable
import android.text.ParcelableSpan
import android.text.Spanned
import com.mkt120.bloggerable.R

interface CreatePostsContract {
    public enum class TYPE(
        val messageResId: Int,
        val positiveButtonResId: Int,
        val negativeButtonResId: Int,
        val neutralButtonResId: Int,
        val isCancelable: Boolean
    ) {
        EXIST_BACK_UP(
            R.string.confirm_dialog_exist_backup_message,
            R.string.confirm_dialog_exist_backup_positive_button,
            R.string.confirm_dialog_exist_backup_negative_button,
            0,
            true
        ),
        BACK_EDIT_POSTS(
            R.string.confirm_dialog_back_post_message,
            R.string.confirm_dialog_positive_button_update,
            R.string.confirm_dialog_negative_button,
            R.string.confirm_dialog_neutral_button,
            true
        ),
        BACK_EDIT_DRAFT(
            R.string.confirm_dialog_back_post_message,
            R.string.confirm_dialog_positive_button_update,
            R.string.confirm_dialog_negative_button,
            R.string.confirm_dialog_neutral_button,
            true
        ),
        DELETE_POST(
            R.string.create_posts_delete_dialog_message,
            android.R.string.yes,
            android.R.string.no,
            0,
            true
        ),

        GET_USER_INFO_FAILED(
            R.string.error_dialog_text,
            R.string.error_dialog_retry,
            R.string.error_dialog_finish,
            0,
            false
        ),
        GET_BLOG_INFO_FAILED(
            R.string.error_dialog_text,
            R.string.error_dialog_retry,
            R.string.error_dialog_finish,
            0,
            false
        ),
        RECEIVE_OBTAIN_POST_ERROR(
            R.string.error_dialog_text,
            R.string.error_dialog_retry,
            R.string.error_dialog_finish,
            0,
            false
        );

        fun isDraft(): Boolean {
            return this == BACK_EDIT_DRAFT
        }

        fun isShowNeutral(): Boolean {
            return neutralButtonResId != 0
        }
    }

    interface View {
        fun setBlogTitle(title: String?)
        fun setBlogContent(content: Spanned?)
        fun replaceContent(left: Int, right: Int, text: String)

        fun showConfirmDialog(type: TYPE)

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
        fun onBackPressed(title: String, html: String): Boolean

        fun onClickConfirmPositive(
            type: TYPE,
            title: String,
            html: String
        )

        fun onClickConfirmNegative(type: TYPE)
        fun onConfirmNeutralButton(
            type: TYPE,
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