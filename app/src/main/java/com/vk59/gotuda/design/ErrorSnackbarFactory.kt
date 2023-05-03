package com.vk59.gotuda.design

import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.vk59.gotuda.R
import com.vk59.gotuda.core.colorAttr

class ErrorSnackbarFactory(private val view: View) {

  fun create(icon: Int, text: String): Snackbar {
    val snackbar = Snackbar.make(view, "   $text", Snackbar.LENGTH_LONG)
    val view = snackbar.view
    val sbTextView = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
    sbTextView.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0)
    sbTextView.setTextColor(view.colorAttr(R.attr.textMain))
    snackbar.setBackgroundTint(view.colorAttr(R.attr.bgMain))
    val params = view.layoutParams as FrameLayout.LayoutParams
    params.gravity = Gravity.TOP
    view.layoutParams = params
    return snackbar
  }
}