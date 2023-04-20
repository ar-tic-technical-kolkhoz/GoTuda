package com.vk59.gotuda.data.model

import com.vk59.gotuda.data.model.PlaceType.STREET
import com.vk59.gotuda.map.model.MyGeoPoint

class PlaceToVisit(
  val id: String,
  val name: String,
  val geoPoint: MyGeoPoint,
  val photoUrl: String,
  val address: String,
  val tags: List<PlaceTag>,
  val type: PlaceType = STREET
)