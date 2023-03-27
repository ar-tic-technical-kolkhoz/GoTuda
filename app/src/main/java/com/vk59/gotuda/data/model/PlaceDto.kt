package com.vk59.gotuda.data.model

import com.vk59.gotuda.data.model.PlaceType.STREET
import com.vk59.gotuda.map.model.GoGeoPoint

class PlaceDto(
  val id: String,
  val geoPoint: GoGeoPoint,
  val type: PlaceType = STREET
)

enum class PlaceType {
  STREET,
  INSIDE
}