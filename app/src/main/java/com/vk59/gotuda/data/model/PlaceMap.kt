package com.vk59.gotuda.data.model

import com.vk59.gotuda.data.model.PlaceType.STREET
import com.vk59.gotuda.map.model.MyGeoPoint

data class PlaceMap(
  val id: String,
  val geoPoint: MyGeoPoint,
  val selected: Boolean = false,
  val type: PlaceType = STREET
)

enum class PlaceType {
  STREET,
  INSIDE
}