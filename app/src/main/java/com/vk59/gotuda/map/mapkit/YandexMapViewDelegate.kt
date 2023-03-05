package com.vk59.gotuda.map.mapkit

import android.content.res.Configuration
import android.view.View
import androidx.fragment.app.Fragment
import com.vk59.gotuda.map.MapViewDelegate
import com.vk59.gotuda.map.model.GoGeoPoint
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapType.MAP
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer

class YandexMapViewDelegate(fragment: Fragment) : MapViewDelegate(fragment) {

  private var userLocationLayer: UserLocationLayer? = null

  private var map: MapView? = null

  override fun initMapView(mapView: View) {
    map = mapView as MapView
    mapView.map.mapType = MAP
    if (fragment.requireContext().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
      mapView.map.isNightModeEnabled = true
    }
  }

  override fun showUserLocation() {
    val mapView = map ?: throw IllegalStateException("Necessary to call initMapView previously")
    val mapKit = MapKitFactory.getInstance()
    mapKit.resetLocationManagerToDefault()
    userLocationLayer = mapKit.createUserLocationLayer(mapView.mapWindow)
    userLocationLayer?.isVisible = true
    userLocationLayer?.isHeadingEnabled = true

    userLocationLayer?.setObjectListener(MainUserLocationObjectListener())
  }

  override fun moveToUserLocation(geoPoint: GoGeoPoint) {
    val mapView = map ?: throw IllegalStateException("Necessary to call initMapView previously")

    mapView.map.move(CameraPosition(Point(geoPoint.latitude, geoPoint.longitude), 16f, 0f, 0f))
  }

  override fun addPlacemark(geoPoint: GoGeoPoint, drawableInt: Int) {

  }
}