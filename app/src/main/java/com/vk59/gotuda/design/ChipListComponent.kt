package com.vk59.gotuda.design

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import com.vk59.gotuda.core.dpToPx
import com.vk59.gotuda.presentation.settings.Chip

typealias ChipItemModel = Pair<String, String>

inline val ChipItemModel.id get() = first
inline val ChipItemModel.name get() = second

class ChipListComponent @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
  defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

  private val flow = Flow(context).apply {
    id = generateViewId()
    setOrientation(Flow.HORIZONTAL)
    setHorizontalAlign(Flow.HORIZONTAL_ALIGN_START)
    setHorizontalGap(dpToPx(8))
    setHorizontalStyle(Flow.CHAIN_PACKED)
    setHorizontalBias(0f)
    setVerticalAlign(Flow.VERTICAL_ALIGN_TOP)
    setVerticalGap(dpToPx(8))
    setVerticalBias(0f)
    setVerticalStyle(Flow.CHAIN_PACKED)
    setWrapMode(Flow.WRAP_CHAIN)
    this@ChipListComponent.addView(this)
  }

  private val chipItems: MutableList<Chip> = mutableListOf()

  fun showChips(chips: List<Chip>) {
    refresh(chips)
    realShowChips()
  }

  private fun refresh(chips: List<Chip>) {
    chipItems.clear()
    chipItems.addAll(chips)
    flow.referencedIds = intArrayOf()
    removeAllViews()
  }

  private fun realShowChips() {
    addView(flow)
    chipItems.forEach {
      val chipComponent = ChipComponent(context).apply {
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        setText(it.name)
        isSelected = it.selected
      }
      chipComponent.id = generateViewId()
      addView(chipComponent)
      flow.addView(chipComponent)
    }
  }
}