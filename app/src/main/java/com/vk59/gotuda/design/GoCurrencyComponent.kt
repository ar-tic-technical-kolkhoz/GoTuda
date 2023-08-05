package com.vk59.gotuda.design

import android.content.Context
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.ColorInt
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import com.vk59.gotuda.R
import com.vk59.gotuda.core.colorAttr
import com.vk59.gotuda.core.dpToPx
import com.vk59.gotuda.databinding.GoCurrencyComponentBinding

class GoCurrencyComponent @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
  defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

  private val binding: GoCurrencyComponentBinding

  init {
    binding = GoCurrencyComponentBinding.inflate(LayoutInflater.from(context), this)
    setBackgroundColor(colorAttr(R.attr.bgIconButton))
    elevation = 5f

    isClickable = true
    binding.root.setPadding(
      dpToPx(12),
      dpToPx(4),
      dpToPx(12),
      dpToPx(4),
    )
  }

  fun setValue(value: CharSequence) {
    binding.value.text = value
  }

  override fun setBackgroundColor(@ColorInt color: Int) {
    val drawable = AppCompatResources.getDrawable(context, R.drawable.go_currency_background) as? RippleDrawable
    drawable?.findDrawableByLayerId(R.id.shape)?.apply {
      this.setTint(color)
    }
    background = drawable
  }
}