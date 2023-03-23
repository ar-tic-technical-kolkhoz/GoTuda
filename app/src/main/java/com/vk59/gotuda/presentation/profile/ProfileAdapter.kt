package com.vk59.gotuda.presentation.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.vk59.gotuda.R
import com.vk59.gotuda.core.dimen
import com.vk59.gotuda.databinding.ItemProfileHeaderBinding
import com.vk59.gotuda.databinding.ItemProfilePlaceBinding
import com.vk59.gotuda.design.ButtonComponent
import com.vk59.gotuda.design.ListComponent
import com.vk59.gotuda.presentation.profile.ProfileListItem.Button
import com.vk59.gotuda.presentation.profile.ProfileListItem.Header
import com.vk59.gotuda.presentation.profile.ProfileListItem.HistoryPlace
import com.vk59.gotuda.presentation.profile.ProfileListItem.Title
import com.vk59.gotuda.presentation.profile.ProfileListItem.TitleValue

private const val HEADER_VIEW_TYPE = 0
private const val BUTTON_VIEW_TYPE = 1
private const val TITLE_VIEW_TYPE = 2
private const val TITLE_VALUE_VIEW_TYPE = 3
private const val HISTORY_PLACE_VIEW_TYPE = 4

class ProfileAdapter(
  private val onEditClick: () -> Unit,
  private val onPlaceClick: (String) -> Unit,
) : ListAdapter<ProfileListItem, ProfileItemViewHolder>(DIFF_UTIL_CALLBACK) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileItemViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    return when (viewType) {
      HEADER_VIEW_TYPE -> HeaderViewHolder(ItemProfileHeaderBinding.inflate(inflater, parent, false))
      BUTTON_VIEW_TYPE -> ButtonViewHolder(
        button = ButtonComponent(parent.context).apply {
          layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
          val padding = context.dimen(R.dimen.mu_2)
          setPadding(padding, padding, padding, padding)
        },
        onButtonClick = onEditClick
      )
      TITLE_VIEW_TYPE -> TitleViewHolder(ListComponent(parent.context).apply {
        layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
      })
      TITLE_VALUE_VIEW_TYPE -> TitleValueViewHolder(ListComponent(parent.context).apply {
        layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
      })
      HISTORY_PLACE_VIEW_TYPE -> HistoryPlaceViewHolder(
        ItemProfilePlaceBinding.inflate(inflater, parent, false),
        onPlaceClick
      )
      else -> throw IllegalStateException()
    }
  }

  override fun onBindViewHolder(holder: ProfileItemViewHolder, position: Int) {
    holder.bind(currentList[position])
  }

  override fun getItemViewType(position: Int): Int {
    return when (currentList[position]) {
      is Header -> HEADER_VIEW_TYPE
      is Button -> BUTTON_VIEW_TYPE
      is Title -> TITLE_VIEW_TYPE
      is TitleValue -> TITLE_VALUE_VIEW_TYPE
      is HistoryPlace -> HISTORY_PLACE_VIEW_TYPE
    }
  }

  companion object {

    val DIFF_UTIL_CALLBACK = object : DiffUtil.ItemCallback<ProfileListItem>() {

      override fun areItemsTheSame(oldItem: ProfileListItem, newItem: ProfileListItem): Boolean {
        return oldItem::class.java == newItem::class.java
      }

      override fun areContentsTheSame(oldItem: ProfileListItem, newItem: ProfileListItem): Boolean {
        return oldItem == newItem
      }
    }
  }
}