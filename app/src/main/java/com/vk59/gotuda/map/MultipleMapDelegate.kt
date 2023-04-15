package com.vk59.gotuda.map

import com.vk59.gotuda.map.model.GoGeoPoint

class MultipleMapDelegate(val delegates: List<MapViewDelegate>) {

  fun moveToUserLocation(geoPoint: GoGeoPoint) {
    delegates.forEach { it.moveToUserLocation(geoPoint) }
  }

  fun addPlacemark(geoPoint: GoGeoPoint, drawableInt: Int) {
    delegates.forEach { it.addPlacemark(geoPoint, drawableInt) }
  }

  fun showUserLocation(geoPoint: GoGeoPoint) {
    delegates.forEach { it.updateUserLocation(geoPoint) }
  }

  fun detach() {
    delegates.forEach { it.detach() }
  }
}