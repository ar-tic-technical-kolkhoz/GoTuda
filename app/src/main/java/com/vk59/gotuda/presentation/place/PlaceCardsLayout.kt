package com.vk59.gotuda.presentation.place

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.vk59.gotuda.data.model.PlaceTag
import com.vk59.gotuda.data.model.PlaceToVisit
import com.vk59.gotuda.databinding.LayoutPlaceCardsBinding
import com.vk59.gotuda.presentation.settings.Chip

class PlaceCardsLayout @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
  defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

  private val binding: LayoutPlaceCardsBinding

  init {
    binding = LayoutPlaceCardsBinding.inflate(LayoutInflater.from(context), this, true)
  }

  fun setOnBackButtonClickListener(onBackClick: () -> Unit) {
    binding.backButton.setOnClickListener { onBackClick.invoke() }
  }

  fun bindPlace(place: PlaceToVisit) {
    binding.apply {
      Glide.with(context).load(placePhoto).into(placePhoto).clearOnDetach()
      placeName.text = place.name
      placeAddress.text = place.address
      placeTags.showChips(place.tags.toChips())
    }
  }
}

private fun List<PlaceTag>.toChips(): List<Chip> {
  return this.map { Chip(it.id, it.name, true) }
}
