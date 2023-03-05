package com.vk59.gotuda.core

import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt

@ColorInt
fun View.colorAttr(@AttrRes id: Int): Int {
  val typedValue = TypedValue()
  val theme = context.theme
  theme.resolveAttribute(id, typedValue, true)
  return typedValue.data
}