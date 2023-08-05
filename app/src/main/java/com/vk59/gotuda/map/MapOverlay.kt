package com.vk59.gotuda.map

import android.view.View
import androidx.annotation.DrawableRes
import com.vk59.gotuda.map.model.MyGeoPoint
import com.yandex.mapkit.transport.masstransit.Route

abstract class MapOverlay() {

  abstract fun attach(mapView: View)

  abstract fun moveToUserLocation(geoPoint: MyGeoPoint)

  abstract fun addPlacemark(
    id: String,
    geoPoint: MyGeoPoint,
    @DrawableRes drawableInt: Int
  )

  abstract fun updateUserLocation(geoPoint: MyGeoPoint)

  abstract fun showRoute(route: Route)

  abstract fun removeRoute()

  abstract fun detach()
}