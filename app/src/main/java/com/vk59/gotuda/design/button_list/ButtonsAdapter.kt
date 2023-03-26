package com.vk59.gotuda.design.button_list

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vk59.gotuda.R
import com.vk59.gotuda.design.button_list.ButtonsAdapter.ButtonVH
import com.vk59.gotuda.core.colorAttr

class ButtonsAdapter : ListAdapter<ButtonUiModel, ButtonVH>(diffUtilCallback) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonVH {
    return ButtonVH(Button(parent.context).apply {
      layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
      setBackgroundResource(R.drawable.button_background)
      setTextColor(parent.colorAttr(R.attr.textOnControlMain))
    })
  }

  override fun onBindViewHolder(holder: ButtonVH, position: Int) {
    holder.bind(currentList[position])
  }

  inner class ButtonVH(private val button: Button) : RecyclerView.ViewHolder(button) {

    @SuppressLint("SetTextI18n")
    fun bind(buttonUiModel: ButtonUiModel) {
      button.text = buttonUiModel.title
      buttonUiModel.subtitle?.let { button.text = "\n$it" }
      button.setOnClickListener {
        buttonUiModel.onClick.invoke()
      }
    }
  }

  companion object {

    val diffUtilCallback = object : DiffUtil.ItemCallback<ButtonUiModel>() {
      override fun areItemsTheSame(oldItem: ButtonUiModel, newItem: ButtonUiModel): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: ButtonUiModel, newItem: ButtonUiModel): Boolean {
        return oldItem == newItem
      }
    }
  }
}