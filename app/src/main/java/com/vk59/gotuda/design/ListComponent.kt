package com.vk59.gotuda.design

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.vk59.gotuda.databinding.ListComponentBinding

class ListComponent @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
  defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

  private val binding: ListComponentBinding

  init {
    binding = ListComponentBinding.inflate(LayoutInflater.from(context), this)
  }

  fun setTitle(text: CharSequence?) {
    binding.title.text = text
  }

  fun setCompanionText(text: CharSequence?) {
    binding.companionText.text = text
  }
}