package com.mkt120.bloggerable

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class AddLabelHistoryDialogFragment : DialogFragment() {
    companion object {
        fun newInstance(): AddLabelHistoryDialogFragment =
            AddLabelHistoryDialogFragment()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(R.string.create_posts_select_label)
        val list = PreferenceManager.labelList.toTypedArray()
        builder.setItems(list) { _, position ->
            val label = list[position]
            if (activity is AddLabelDialogFragment.OnClickListener) {
                (activity as AddLabelDialogFragment.OnClickListener).addLabel(label)
            }
        }
        builder.setNegativeButton(android.R.string.cancel) { _, _ ->
            dismiss()
        }

        return builder.create()
    }
}