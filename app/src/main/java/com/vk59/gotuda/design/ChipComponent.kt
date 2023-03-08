package com.vk59.gotuda.design

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.vk59.gotuda.R
import com.vk59.gotuda.core.colorAttr
import com.vk59.gotuda.core.setTextSizeSp

class ChipComponent @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
  defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

  private val textView: TextView

  init {
    textView = TextView(context).apply {
      setTextColor(colorAttr(R.attr.textMain))
      setTextSizeSp(16f)
      gravity = TEXT_ALIGNMENT_CENTER
      layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
        setMargins(18, 8, 18, 8)
      }
    }
    addView(textView)
    background = AppCompatResources.getDrawable(context, R.drawable.chip_selection_bg)
  }

  fun setText(text: CharSequence?) {
    textView.text = text
  }

  override fun setSelected(selected: Boolean) {
    super.setSelected(selected)
    alpha = if (selected) { 1f } else { 0.5f }
  }
}