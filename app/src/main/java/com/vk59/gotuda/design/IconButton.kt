package com.vk59.gotuda.design

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.vk59.gotuda.R
import com.vk59.gotuda.databinding.ButtonIconBinding

class IconButton @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
  defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

  private val binding: ButtonIconBinding

  init {
    binding = ButtonIconBinding.inflate(LayoutInflater.from(context), this)
    background = AppCompatResources.getDrawable(context, R.drawable.white_button_bg)
    isClickable = true
  }

  fun setIconResource(@DrawableRes icon: Int) {
    binding.icon.setImageResource(icon)
  }
}