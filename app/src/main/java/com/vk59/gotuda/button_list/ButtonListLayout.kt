package com.vk59.gotuda.button_list

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ButtonListLayout @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
  defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

  private val list: RecyclerView
  private val adapter = ButtonsAdapter()

  init {
    list = RecyclerView(context).apply {
      layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
    }
    addView(list)
    list.adapter = adapter
    list.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
    list.addItemDecoration(ButtonItemDecoration())
  }

  fun addButtons(buttons: List<ButtonUiModel>) {
    adapter.submitList(buttons)
  }
}