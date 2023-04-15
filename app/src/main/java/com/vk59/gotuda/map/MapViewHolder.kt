package com.vk59.gotuda.map

import android.content.Context
import android.view.View
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.vk59.gotuda.map.model.MyGeoPoint
import java.lang.ref.WeakReference

abstract class MapViewHolder(private var _fragment: WeakReference<Fragment>) {

  protected val fragment: Fragment
    get() = _fragment.get() ?: throw IllegalStateException("Fragment is not attached")

  protected val fragmentContext: Context
    get() = fragment.requireContext()

  abstract fun attach(mapView: View)

  abstract fun moveToUserLocation(geoPoint: MyGeoPoint)

  abstract fun addPlacemark(geoPoint: MyGeoPoint, @DrawableRes drawableInt: Int)

  abstract fun updateUserLocation(geoPoint: MyGeoPoint)

  open fun detach() {
    _fragment.clear()
  }
}