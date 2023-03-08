package com.vk59.gotuda.design

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.vk59.gotuda.R
import com.vk59.gotuda.databinding.ButtonComponentBinding

class ButtonComponent @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
  defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

  private val binding: ButtonComponentBinding

  init {
    binding = ButtonComponentBinding.inflate(LayoutInflater.from(context), this)
    background = AppCompatResources.getDrawable(context, R.drawable.button_background)
    isClickable = true
  }

  fun setTitle(text: CharSequence) {
    binding.title.text = text
  }

  fun setSubtitle(subtitle: CharSequence) {
    binding.subtitle.text = subtitle
    binding.subtitle.isVisible = true
  }

  fun setTitleSizeSp(size: Float) {
    binding.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
  }
}