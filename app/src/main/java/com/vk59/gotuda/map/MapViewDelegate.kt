package com.vk59.gotuda.map

import android.view.View
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.vk59.gotuda.map.model.GoGeoPoint

abstract class MapViewDelegate(protected val fragment: Fragment) {

  abstract fun initMapView(mapView: View)

  abstract fun moveToUserLocation(geoPoint: GoGeoPoint)

  abstract fun addPlacemark(geoPoint: GoGeoPoint, @DrawableRes drawableInt: Int)
  abstract fun showUserLocation()
}