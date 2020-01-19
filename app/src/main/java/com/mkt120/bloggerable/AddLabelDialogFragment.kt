package com.mkt120.bloggerable

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_add_label.view.*


class AddLabelDialogFragment : DialogFragment() {
    companion object {
        fun newInstance(): AddLabelDialogFragment =
            AddLabelDialogFragment()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(R.string.create_posts_input_label)
        val rootView =
            requireActivity().layoutInflater.inflate(R.layout.dialog_add_label, null, false)
        builder.setView(rootView)
        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            rootView?.let {
                if (it.edit_text.text.isNotEmpty() && activity is OnClickListener) {
                    (activity as OnClickListener).addLabel(it.edit_text.text.toString())
                }
            }
        }
        builder.setNegativeButton(android.R.string.cancel) { _, _ ->
            dismiss()
        }
        return builder.create()
    }

    interface OnClickListener {
        fun addLabel(label: String)
    }
}
