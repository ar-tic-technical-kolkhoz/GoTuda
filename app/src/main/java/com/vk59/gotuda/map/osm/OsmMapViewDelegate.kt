package com.vk59.gotuda.map.osm

import android.content.Context
import android.preference.PreferenceManager
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.vk59.gotuda.R.drawable
import com.vk59.gotuda.map.MapViewDelegate
import com.vk59.gotuda.map.model.GoGeoPoint
import com.vk59.gotuda.map.model.MapNotAttachedToWindowException
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class OsmMapViewDelegate(fragment: Fragment) : MapViewDelegate(fragment) {

  private var map: MapView? = null

  override fun attach(mapView: View) {
    map = mapView as? MapView? ?: throw IllegalStateException("Incorrect type of MapView ${mapView.javaClass}")
  }

  override fun updateUserLocation(geoPoint: GoGeoPoint) {
    Configuration.getInstance().load(
      fragmentContext.applicationContext,
      PreferenceManager.getDefaultSharedPreferences(fragmentContext.applicationContext)
    )
    val mapView = map ?: throw MapNotAttachedToWindowException()
    mapView.setTileSource(TileSourceFactory.MAPNIK)
    val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(fragmentContext), mapView)
    locationOverlay.setPersonIcon(ContextCompat.getDrawable(requireContext(), drawable.user_geo)?.toBitmap())
    locationOverlay.setDirectionIcon(ContextCompat.getDrawable(requireContext(), drawable.user_geo)?.toBitmap())
    locationOverlay.enableMyLocation()
    mapView.overlays.add(locationOverlay)
  }

  private fun requireContext(): Context {
    return fragment.requireContext()
  }

  override fun moveToUserLocation(geoPoint: GoGeoPoint) {
    val mapView = map ?: throw MapNotAttachedToWindowException()

    mapView.controller.animateTo(GeoPoint(geoPoint.latitude, geoPoint.longitude), 18.0, 1000)
  }

  override fun addPlacemark(geoPoint: GoGeoPoint, drawableInt: Int) {

  }

  override fun detach() {
    super.detach()
    map = null
  }
}