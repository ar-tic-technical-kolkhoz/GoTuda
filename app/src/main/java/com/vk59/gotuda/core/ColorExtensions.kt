package com.vk59.gotuda.core

import android.content.Context
import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt

@ColorInt
fun View.colorAttr(@AttrRes id: Int): Int {
  return context.colorAttr(id)
}

@ColorInt
fun Context.colorAttr(@AttrRes id: Int): Int {
  val typedValue = TypedValue()
  val theme = theme
  theme.resolveAttribute(id, typedValue, true)
  return typedValue.data
}