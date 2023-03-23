package com.vk59.gotuda.presentation.profile

import android.graphics.Typeface.DEFAULT_BOLD
import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.vk59.gotuda.R
import com.vk59.gotuda.core.colorAttr
import com.vk59.gotuda.databinding.ItemProfileHeaderBinding
import com.vk59.gotuda.databinding.ItemProfilePlaceBinding
import com.vk59.gotuda.design.ButtonComponent
import com.vk59.gotuda.design.ListComponent
import com.vk59.gotuda.design.ListComponent.DividerType.TOP
import com.vk59.gotuda.presentation.profile.ProfileListItem.Button
import com.vk59.gotuda.presentation.profile.ProfileListItem.Header
import com.vk59.gotuda.presentation.profile.ProfileListItem.HistoryPlace
import com.vk59.gotuda.presentation.profile.ProfileListItem.Title
import com.vk59.gotuda.presentation.profile.ProfileListItem.TitleValue

abstract class ProfileItemViewHolder(itemView: View) : ViewHolder(itemView) {

  abstract fun bind(item: ProfileListItem)
}

class HeaderViewHolder(
  private val binding: ItemProfileHeaderBinding
) : ProfileItemViewHolder(binding.root) {

  override fun bind(item: ProfileListItem) {
    item as? Header ?: throw IllegalStateException()
    binding.userName.text = item.name
    Glide.with(binding.root.context).load(item.photoUrl)
      .into(binding.userPhoto)
  }
}

class ButtonViewHolder(
  private val button: ButtonComponent,
  private val onButtonClick: () -> Unit
) : ProfileItemViewHolder(button) {

  override fun bind(item: ProfileListItem) {
    item as? Button ?: throw IllegalStateException()

    button.setTitle(item.text)
    button.setOnClickListener { onButtonClick.invoke() }
    button.setBackgroundColor(button.colorAttr(R.attr.controlMinor))
  }
}

class TitleViewHolder(private val component: ListComponent) : ProfileItemViewHolder(component) {

  override fun bind(item: ProfileListItem) {
    item as? Title ?: throw IllegalStateException()

    component.setTitle(item.text)
    component.setTitleSizeSp(24f)
    component.setTypeface(DEFAULT_BOLD)
  }
}

class TitleValueViewHolder(private val component: ListComponent) : ProfileItemViewHolder(component) {

  override fun bind(item: ProfileListItem) {
    item as? TitleValue ?: throw IllegalStateException()

    component.setTitle(item.title)
    component.setCompanionText(item.value)
    component.setDivider(TOP)
  }
}

class HistoryPlaceViewHolder(
  private val binding: ItemProfilePlaceBinding,
  private val onGoButtonClick: (String) -> Unit
) : ProfileItemViewHolder(binding.root) {

  override fun bind(item: ProfileListItem) {
    item as? HistoryPlace ?: throw IllegalStateException()

    Glide.with(binding.root.context).load(item.place.photoUrl)
      .into(binding.image)

    binding.name.text = item.place.name

    binding.button.setOnClickListener {
      onGoButtonClick.invoke(item.place.id)
    }
  }
}
