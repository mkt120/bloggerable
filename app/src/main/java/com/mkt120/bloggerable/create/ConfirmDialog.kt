package com.mkt120.bloggerable.create

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class ConfirmDialog : DialogFragment() {

    companion object {
        private const val EXTRA_TYPE = "EXTRA_TYPE"
        fun newInstance(type: CreatePostsContract.TYPE): ConfirmDialog = ConfirmDialog().apply {
            val bundle = Bundle().apply {
                putSerializable(EXTRA_TYPE, type)
            }
            arguments = bundle
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val type = arguments!!.getSerializable(EXTRA_TYPE) as CreatePostsContract.TYPE
        builder.setMessage(type.messageResId)
        builder.setPositiveButton(type.positiveButtonResId) { _, _ ->
            if (activity is OnClickListener) {
                (activity as OnClickListener).onConfirmPositiveClick(type)
            }
            dismiss()
        }

        builder.setNegativeButton(type.negativeButtonResId) { _, _ ->
            if (activity is OnClickListener) {
                (activity as OnClickListener).onConfirmNegativeClick(type)
            }
            dismiss()
        }
        if (type.isShowNeutral()) {
            builder.setNeutralButton(type.neutralButtonResId) { _, _ ->
                if (activity is OnClickListener) {
                    (activity as OnClickListener).onConfirmNeutralClick(type)
                }
            }
        }
        isCancelable = type.isCancelable

        return builder.create()
    }

    interface OnClickListener {
        fun onConfirmPositiveClick(type: CreatePostsContract.TYPE)
        fun onConfirmNegativeClick(type: CreatePostsContract.TYPE)
        fun onConfirmNeutralClick(type: CreatePostsContract.TYPE)
    }
}
