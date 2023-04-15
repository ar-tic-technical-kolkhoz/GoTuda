package com.vk59.gotuda.map.mapkit

import android.content.res.Configuration
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.vk59.gotuda.di.SimpleDi
import com.vk59.gotuda.map.MapViewDelegate
import com.vk59.gotuda.map.model.GoGeoPoint
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.runtime.image.ImageProvider

class YandexMapViewDelegate(fragment: Fragment) : MapViewDelegate(fragment) {

  private val userLocationLayer: UserLocationLayer?
    get() = SimpleDi.userLocationLayer

  private var map: MapView? = null

  private var mapObjects: MapObjectCollection? = null

  private val placeMarks = mutableSetOf<PlacemarkMapObject>()

  override fun attach(mapView: View) {
    map = mapView as MapView
    mapView.map.isRotateGesturesEnabled = false
    if (fragment.requireContext().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
      mapView.map.isNightModeEnabled = true
    }
    mapView.map.setMapStyle(
      "[" +
          "{" +
          "    \"elements\": \"label\"," +
          "    \"stylers\": {" +
          "      \"visibility\": \"off\"" +
          "  }" +
          "}" +
          "]"
    )
    requireUserLocationLayer()
  }

  override fun showUserLocation() {
    val layer = requireUserLocationLayer()
    layer.isVisible = true
    layer.isHeadingEnabled = true

    layer.setObjectListener(MainUserLocationObjectListener())
  }

  private fun requireUserLocationLayer(): UserLocationLayer {
    val mapWindow = requireMap().mapWindow
    val mapKit = MapKitFactory.getInstance()
    val layer = userLocationLayer
    return if (layer != null && layer.isValid) {
      layer
    } else {
      mapKit.resetLocationManagerToDefault()
      mapKit.createUserLocationLayer(mapWindow).also {
        SimpleDi.userLocationLayer = it
      }
    }
  }

  override fun moveToUserLocation(geoPoint: GoGeoPoint) {
    val mapView = requireMap()

    mapView.map.move(
      CameraPosition(Point(geoPoint.latitude, geoPoint.longitude), 16f, 0f, 0f),
      Animation(Animation.Type.SMOOTH, 0.3f),
      null
    )
  }

  override fun addPlacemark(geoPoint: GoGeoPoint, drawableInt: Int) {
    placeMarks.add(
      requireMapObjectsCollection().addPlacemark(
        Point(geoPoint.latitude, geoPoint.longitude),
        ImageProvider.fromBitmap(AppCompatResources.getDrawable(fragmentContext, drawableInt)?.toBitmap())
      )
    )
  }

  private fun requireMapObjectsCollection(): MapObjectCollection {
    return mapObjects ?: (requireMap().map.mapObjects).addCollection().also { mapObjects = it }
  }

  private fun requireMap(): MapView {
    return map ?: throw IllegalStateException("ecessary to call initMapView previously")
  }

  override fun detach() {
    super.detach()
    placeMarks.forEach { mapObjects?.remove(it) }
    placeMarks.clear()
    map = null
    mapObjects = null
  }
}