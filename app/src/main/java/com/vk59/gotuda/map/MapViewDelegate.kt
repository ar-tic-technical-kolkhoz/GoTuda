package com.vk59.gotuda.map

import android.content.Context
import android.view.View
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.vk59.gotuda.map.model.GoGeoPoint

abstract class MapViewDelegate(private var _fragment: Fragment? = null) {

  protected val fragment: Fragment
    get() = _fragment ?: throw IllegalStateException("Fragment is not attached")

  protected val fragmentContext: Context
    get() = fragment.requireContext()

  abstract fun attach(mapView: View)

  abstract fun moveToUserLocation(geoPoint: GoGeoPoint)

  abstract fun addPlacemark(geoPoint: GoGeoPoint, @DrawableRes drawableInt: Int)

  abstract fun showUserLocation()

  open fun detach() {
    _fragment = null
  }
}