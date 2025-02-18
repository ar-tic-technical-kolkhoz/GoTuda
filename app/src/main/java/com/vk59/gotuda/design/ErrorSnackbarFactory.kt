package com.vk59.gotuda.design

import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.google.android.material.snackbar.Snackbar
import com.vk59.gotuda.R
import com.vk59.gotuda.core.colorAttr
import com.vk59.gotuda.core.dpToPx

class ErrorSnackbarFactory(private val view: View) {

  fun create(@DrawableRes icon: Int, text: String): Snackbar {
    val snackbar = Snackbar.make(view, "   $text", Snackbar.LENGTH_LONG)
    val view = snackbar.view
    val sbTextView = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
    sbTextView.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0)
    sbTextView.setTextColor(view.colorAttr(R.attr.onIconButton))
    snackbar.setBackgroundTint(view.colorAttr(R.attr.bgIconButton))
    val params = view.layoutParams as FrameLayout.LayoutParams
    params.gravity = Gravity.TOP
    params.topMargin = view.dpToPx(20)
    view.layoutParams = params
    return snackbar
  }
}