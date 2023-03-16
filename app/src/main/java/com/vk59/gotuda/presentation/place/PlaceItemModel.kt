package com.vk59.gotuda.presentation.place

import com.vk59.gotuda.presentation.settings.Chip

data class PlaceItemModel(
  val imageUrl: String,
  val name: String,
  val chips: List<Chip>,
  val address: String
)