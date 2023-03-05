package com.vk59.gotuda.map

import androidx.fragment.app.Fragment
import com.vk59.gotuda.map.model.GoGeoPoint

abstract class MapViewDelegate(protected val fragment: Fragment) {

  abstract fun initMap()

  abstract fun switchToUserLocation(geoPoint: GoGeoPoint)
}