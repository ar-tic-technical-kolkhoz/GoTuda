package com.vk59.gotuda.core

import android.content.Context
import android.util.TypedValue
import android.widget.TextView
import androidx.annotation.DimenRes

fun TextView.setTextSizeSp(size: Float) {
  setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
}

fun Context.dimen(@DimenRes dimen: Int): Int {
  return resources.getDimension(dimen).toInt()
}