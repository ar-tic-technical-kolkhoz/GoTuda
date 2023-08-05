package com.vk59.gotuda.core

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.DimenRes
import androidx.annotation.Dimension
import androidx.fragment.app.Fragment

fun TextView.setTextSizeSp(size: Float) {
  setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
}

@Dimension
fun Context.dimen(@DimenRes dimen: Int): Int {
  return resources.getDimension(dimen).toInt()
}

@Dimension
fun View.dimen(@DimenRes dimen: Int): Int {
  return context.dimen(dimen)
}

fun View.dpToPx(@Dimension(unit = Dimension.DP) dp: Int): Int {
  val r = context.resources
  return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), r.displayMetrics).toInt()
}

fun Fragment.dpToPx(@Dimension(unit = Dimension.DP) dp: Int): Int {
  val r = this.context?.resources
  return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), r?.displayMetrics).toInt()
}