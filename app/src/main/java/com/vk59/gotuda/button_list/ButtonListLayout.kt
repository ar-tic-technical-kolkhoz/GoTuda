package com.vk59.gotuda.button_list

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vk59.gotuda.R.layout
import com.vk59.gotuda.databinding.LayoutButtonListBinding

class ButtonListLayout @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
  defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

  private val binding: LayoutButtonListBinding
  private val adapter = ButtonsAdapter()

  init {
    inflate(context, layout.layout_button_list, this)
    binding = LayoutButtonListBinding.bind(this)
    binding.list.adapter = adapter
    binding.list.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
  }

  fun addButtons(buttons: List<ButtonUiModel>) {
    adapter.submitList(buttons)
  }
}