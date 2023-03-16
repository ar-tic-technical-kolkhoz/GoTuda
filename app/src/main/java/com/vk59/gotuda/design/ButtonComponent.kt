package com.vk59.gotuda.design

import android.content.Context
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.vk59.gotuda.R
import com.vk59.gotuda.core.colorAttr
import com.vk59.gotuda.databinding.ButtonComponentBinding

class ButtonComponent @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
  defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

  private val binding: ButtonComponentBinding

  init {
    val a = context.obtainStyledAttributes(attrs, R.styleable.ButtonComponent, defStyleAttr, defStyleRes)
    val colorAttr = a.getColor(R.styleable.ButtonComponent_component_background, colorAttr(R.attr.controlMain))
    val title = a.getString(R.styleable.ButtonComponent_component_text_title)
    val subtitle = a.getString(R.styleable.ButtonComponent_component_text_subtitle)
    val textColor = a.getColor(R.styleable.ButtonComponent_component_text_color, colorAttr(R.attr.textOnControlMain))
    val titleTextSize = a.getDimension(
      R.styleable.ButtonComponent_component_title_text_size,
      context.resources.getDimension(R.dimen.button_component_text_size_default)
    )
    a.recycle()


    binding = ButtonComponentBinding.inflate(LayoutInflater.from(context), this)
    val drawable = AppCompatResources.getDrawable(context, R.drawable.button_background) as? RippleDrawable
    drawable?.findDrawableByLayerId(R.id.shape)?.apply {
      this.setTint(colorAttr)
    }
    background = drawable

    if (title != null) {
      setTitle(title)
    }
    if (subtitle != null) {
      setSubtitle(subtitle)
    }
    binding.title.setTextColor(textColor)
    binding.subtitle.setTextColor(textColor)
    setTitleSizePx(titleTextSize)
    isClickable = true
  }

  fun setTitle(text: CharSequence) {
    binding.title.text = text
  }

  fun setSubtitle(subtitle: CharSequence) {
    binding.subtitle.text = subtitle
    binding.subtitle.isVisible = true
  }

  fun setTitleSizePx(size: Float) {
    binding.title.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
  }

  fun setTitleSizeSp(size: Float) {
    binding.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
  }
}