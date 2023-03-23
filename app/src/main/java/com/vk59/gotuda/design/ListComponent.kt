package com.vk59.gotuda.design

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.vk59.gotuda.databinding.ListComponentBinding
import com.vk59.gotuda.design.ListComponent.DividerType.BOTTOM
import com.vk59.gotuda.design.ListComponent.DividerType.TOP
import com.vk59.gotuda.design.ListComponent.DividerType.TOP_AND_BOTTOM

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

  fun setTitleSizeSp(size: Float) {
    binding.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
  }

  fun setTypeface(typeface: Typeface) {
    binding.title.typeface = typeface
  }

  fun setDivider(dividerType: DividerType) {
    when (dividerType) {
      TOP -> {
        binding.topDivider.isVisible = true
        binding.bottomDivider.isVisible = false
      }
      BOTTOM -> {
        binding.topDivider.isVisible = false
        binding.bottomDivider.isVisible = true
      }
      TOP_AND_BOTTOM -> {
        binding.topDivider.isVisible = true
        binding.bottomDivider.isVisible = true
      }
    }
  }

  fun setCompanionText(text: CharSequence?) {
    binding.companionText.text = text
  }

  enum class DividerType {
    TOP,
    BOTTOM,
    TOP_AND_BOTTOM
  }
}