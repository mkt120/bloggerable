package com.mkt120.bloggerable.create

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import com.mkt120.bloggerable.R
import kotlinx.android.synthetic.main.dialog_add_label.view.*

class AddLabelDialogFragment : DialogFragment(), DialogInterface.OnShowListener {
    companion object {
        fun newInstance(): AddLabelDialogFragment =
            AddLabelDialogFragment()
    }

    private var rootView: View? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(R.string.create_posts_input_label)
        rootView =
            requireActivity().layoutInflater.inflate(R.layout.dialog_add_label, null, false)
        builder.setView(rootView)
        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            rootView?.let {
                if (it.edit_text.text.isNotEmpty() && activity is OnClickListener) {
                    (activity as OnClickListener).onClickAddLabel(it.edit_text.text.toString())
                }
            }
        }
        builder.setNegativeButton(android.R.string.cancel) { _, _ ->
            dismiss()
        }
        val dialog = builder.create()
        dialog.setOnShowListener(this)
        return dialog
    }

    override fun onShow(p0: DialogInterface?) {
        rootView!!.edit_text.requestFocus()
        val inputMethodManager =
            activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        inputMethodManager!!.showSoftInput(rootView!!.edit_text, 0)
    }

    interface OnClickListener {
        fun onClickAddLabel(label: String)
    }
}
