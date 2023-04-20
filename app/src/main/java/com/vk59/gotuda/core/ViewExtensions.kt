package com.vk59.gotuda.core

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.DimenRes
import androidx.annotation.Dimension

fun TextView.setTextSizeSp(size: Float) {
  setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
}

fun Context.dimen(@DimenRes dimen: Int): Int {
  return resources.getDimension(dimen).toInt()
}

fun View.dpToPx(@Dimension(unit = Dimension.DP) dp: Int): Int {
  val r = context.resources
  return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), r.displayMetrics).toInt()
}