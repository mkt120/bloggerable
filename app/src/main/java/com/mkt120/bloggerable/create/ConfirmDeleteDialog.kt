package com.mkt120.bloggerable.create

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.mkt120.bloggerable.R

class ConfirmDeleteDialog : DialogFragment() {
    companion object {
        fun newInstance(): ConfirmDeleteDialog =
            ConfirmDeleteDialog()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: android.app.AlertDialog.Builder =
            android.app.AlertDialog.Builder(requireContext())
        builder.setMessage(R.string.create_posts_delete_dialog_message)
            .setPositiveButton(android.R.string.yes) { _, _ ->
                if (activity is CreatePostsActivity) {
                    (activity as CreatePostsActivity).onClickDelete()
                }
                dismiss()
            }
            .setNegativeButton(android.R.string.no) { _, _ ->
                dismiss()
            }
        return builder.create()
    }
}
