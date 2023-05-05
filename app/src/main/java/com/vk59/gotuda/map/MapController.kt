package com.vk59.gotuda.map

import android.view.View
import com.vk59.gotuda.map.actions.MapActionsListener
import com.vk59.gotuda.map.mapkit.MapOverlayFactory
import com.vk59.gotuda.map.model.MyGeoPoint
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.transport.masstransit.Route
import javax.inject.Inject

class MapController @Inject constructor(
  private val mapOverlayFactory: MapOverlayFactory,
) {

  private val holders: MutableList<MapViewHolder> = mutableListOf()

  fun attachViews(mapViews: List<View>, initialGeoPoint: MyGeoPoint?, listener: MapActionsListener) {
    mapViews.forEach { mapView ->
      when (mapView) {
        is MapView -> {
          val holder = mapOverlayFactory.create(initialGeoPoint, listener)
          holders.add(holder)
          holder.attach(mapView)
        }
      }
    }
  }

  fun moveToUserLocation(geoPoint: MyGeoPoint) {
    holders.forEach { it.moveToUserLocation(geoPoint) }
  }

  fun addPlacemark(id: String, geoPoint: MyGeoPoint, drawableInt: Int) {
    holders.forEach { it.addPlacemark(id, geoPoint, drawableInt) }
  }

  fun showUserLocation(geoPoint: MyGeoPoint) {
    holders.forEach { it.updateUserLocation(geoPoint) }
  }

  fun showRoute(route: Route) {
    holders.forEach { it.showRoute(route) }
  }

  fun removeRoute() {
    holders.forEach { it.removeRoute() }
  }

  fun detach() {
    holders.forEach { it.detach() }
    holders.clear()
  }
}