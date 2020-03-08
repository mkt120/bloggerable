package com.mkt120.bloggerable.create

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.mkt120.bloggerable.R

class ConfirmDialog : DialogFragment() {

    companion object {
        private const val EXTRA_TYPE = "EXTRA_TYPE"
        const val TYPE_CREATE = 1
        const val TYPE_EDIT_POSTS = 2
        const val TYPE_EDIT_DRAFT = 3
        fun newInstance(type: Int): ConfirmDialog = ConfirmDialog().apply {
            val bundle = Bundle().apply {
                putInt(EXTRA_TYPE, type)
            }
            arguments = bundle
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(R.string.create_posts_dialog_message)
        val type = arguments!!.getInt(EXTRA_TYPE)
        if (type == TYPE_CREATE) {
            builder.setPositiveButton(R.string.create_posts_dialog_positive_button_create_draft) { _, _ ->
                if (activity is CreatePostsActivity) {
                    (activity as CreatePostsActivity).onConfirmPositiveClick(
                        isCreatePost = true,
                        isDraft = false
                    )
                }
                dismiss()
            }
        } else {
            builder.setPositiveButton(R.string.create_posts_dialog_positive_button_update) { _, _ ->
                if (activity is OnClickListener) {
                    val isDraft = type == TYPE_EDIT_DRAFT
                    (activity as OnClickListener).onConfirmPositiveClick(false, isDraft)
                }
                dismiss()
            }
        }

        builder.setNegativeButton(R.string.create_posts_dialog_negative_button) { _, _ ->
            if (activity is OnClickListener) {
                (activity as OnClickListener).onConfirmNegativeClick()
            }
            dismiss()
        }
        builder.setNeutralButton(android.R.string.cancel, null)
        return builder.create()
    }

    interface OnClickListener {
        fun onConfirmPositiveClick(isCreatePost: Boolean, isDraft: Boolean)
        fun onConfirmNegativeClick()
    }
}
