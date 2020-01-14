package com.mkt120.bloggerable

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_add_label.view.*

class AddLabelDialog : DialogFragment() {
    companion object {
        fun newInstance(): AddLabelDialog =
            AddLabelDialog()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.create_posts_input_label)

        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_label, null)
        builder.setView(view)
        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            if (view.edit_text.text.isNotEmpty() && activity is OnClickListener) {
                (activity as OnClickListener).addLabel(view.edit_text.text.toString())
            }
        }
        return builder.create()
    }

    public interface OnClickListener {
        fun addLabel(label: String)
    }
}
