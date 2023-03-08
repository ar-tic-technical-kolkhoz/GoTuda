package com.vk59.gotuda.core

import android.util.TypedValue
import android.widget.TextView

fun TextView.setTextSizeSp(size: Float) {
  setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
}