package com.mkt120.bloggerable.create

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import com.mkt120.bloggerable.R

class LabelView(context: Context): AppCompatTextView(context) {
    init {
        val verMargin = resources.getDimensionPixelSize(R.dimen.label_margin_hor)
        val horiPadding = resources.getDimensionPixelSize(R.dimen.label_padding_hor)
        val verPadding = resources.getDimensionPixelSize(R.dimen.label_padding_ver)
        setPadding(horiPadding, verPadding, horiPadding, verPadding)
        setBackgroundResource(R.drawable.label_background)
        val lp = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        lp.leftMargin = verMargin
        lp.rightMargin = verMargin
        layoutParams = lp
    }
}