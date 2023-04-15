package com.vk59.gotuda.map

import android.view.View
import androidx.fragment.app.Fragment
import com.vk59.gotuda.map.mapkit.YandexMapViewHolder
import com.vk59.gotuda.map.model.MyGeoPoint
import com.vk59.gotuda.map.osm.OsmMapViewHolder
import com.yandex.mapkit.mapview.MapView
import java.lang.ref.WeakReference

class MapController {

  private val holders: MutableList<MapViewHolder> = mutableListOf()

  fun attachViews(fragment: Fragment, mapViews: List<View>, initialGeoPoint: MyGeoPoint?) {
    val fragmentRef = WeakReference(fragment)
    mapViews.forEach { mapView ->
      when(mapView) {
        is MapView -> {
          val holder = YandexMapViewHolder(fragmentRef, initialGeoPoint)
          holders.add(holder)
          holder.attach(mapView)
        }
        is org.osmdroid.views.MapView -> {
          val delegate = OsmMapViewHolder(fragmentRef)
          holders.add(delegate)
          delegate.attach(mapView)
        }
      }
    }
  }

  fun moveToUserLocation(geoPoint: MyGeoPoint) {
    holders.forEach { it.moveToUserLocation(geoPoint) }
  }

  fun addPlacemark(geoPoint: MyGeoPoint, drawableInt: Int) {
    holders.forEach { it.addPlacemark(geoPoint, drawableInt) }
  }

  fun showUserLocation(geoPoint: MyGeoPoint) {
    holders.forEach { it.updateUserLocation(geoPoint) }
  }

  fun detach() {
    holders.forEach { it.detach() }
    holders.clear()
  }
}