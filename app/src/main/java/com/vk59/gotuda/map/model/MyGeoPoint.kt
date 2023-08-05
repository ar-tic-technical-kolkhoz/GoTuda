package com.vk59.gotuda.map.model

import kotlinx.serialization.Serializable

@Serializable
class MyGeoPoint(val latitude: Double, val longitude: Double) {

  companion object {

    val DEFAULT = MyGeoPoint(59.956584, 30.310862)
  }
}