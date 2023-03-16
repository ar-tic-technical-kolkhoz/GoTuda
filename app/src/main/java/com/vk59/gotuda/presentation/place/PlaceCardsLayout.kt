package com.vk59.gotuda.presentation.place

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.vk59.gotuda.databinding.LayoutPlaceCardsBinding

class PlaceCardsLayout @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
  defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

  private val binding: LayoutPlaceCardsBinding

  init {
    binding = LayoutPlaceCardsBinding.inflate(LayoutInflater.from(context), this, true)
  }
}