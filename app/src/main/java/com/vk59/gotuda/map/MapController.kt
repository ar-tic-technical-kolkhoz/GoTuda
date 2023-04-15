package com.vk59.gotuda.map

import android.view.View
import androidx.fragment.app.Fragment
import com.vk59.gotuda.map.mapkit.YandexMapViewHolder
import com.vk59.gotuda.map.model.GoGeoPoint
import com.vk59.gotuda.map.osm.OsmMapViewHolder
import com.yandex.mapkit.mapview.MapView
import java.lang.ref.WeakReference

class MapController {

  private val delegates: MutableList<MapViewHolder> = mutableListOf()

  fun attachViews(fragment: Fragment, mapViews: List<View>) {
    val fragmentRef = WeakReference(fragment)
    mapViews.forEach { mapView ->
      when(mapView) {
        is MapView -> {
          val delegate = YandexMapViewHolder(fragmentRef)
          delegates.add(delegate)
          delegate.attach(mapView)
        }
        is org.osmdroid.views.MapView -> {
          val delegate = OsmMapViewHolder(fragmentRef)
          delegates.add(delegate)
          delegate.attach(mapView)
        }
      }
    }
  }

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
    delegates.clear()
  }
}