package com.vk59.gotuda.map.mapkit

import android.content.Context
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.vk59.gotuda.R
import com.vk59.gotuda.core.colorAttr
import com.vk59.gotuda.core.fromDrawable
import com.vk59.gotuda.design.WalkRouteInfoView
import com.vk59.gotuda.map.MapViewHolder
import com.vk59.gotuda.map.actions.MapAction.SinglePlaceTap
import com.vk59.gotuda.map.actions.MapActionsListener
import com.vk59.gotuda.map.model.MapNotAttachedToWindowException
import com.vk59.gotuda.map.model.MyGeoPoint
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.PolylineMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.transport.masstransit.Route
import com.yandex.runtime.image.ImageProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ActivityContext

class MapOverlay @AssistedInject constructor(
  @ActivityContext
  private val context: Context,
  @Assisted
  private val initialGeoPoint: MyGeoPoint?,
  @Assisted
  private val mapActionsListener: MapActionsListener,
) : MapViewHolder() {

  private val handler: Handler = Handler(Looper.getMainLooper())

  private var userLocation: MapObjectCollection? = null

  private var userLocationPlacemark: PlacemarkMapObject? = null

  private var map: MapView? = null

  private var mapObjects: MapObjectCollection? = null
  private var routesCollection: MapObjectCollection? = null
  private var routePolyline: PolylineMapObject? = null
  private var badgeObject: PlacemarkMapObject? = null

  private val idToPlaceMarks = mutableMapOf<String, PlacemarkMapObject>()
  private val placeMarksToId = mutableMapOf<PlacemarkMapObject, String>()

  private val walkRouteInfoView = WalkRouteInfoView(context)

  private val placeMarkTapListener = MapObjectTapListener { mapObject, _ ->
    placeMarksToId[mapObject]?.let { mapActionsListener.handleMapAction(SinglePlaceTap(it)) }
    true
  }

  override fun attach(mapView: View) {
    map = mapView as MapView
    mapView.map.isRotateGesturesEnabled = false
    if (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
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
    initialGeoPoint?.let {
      updateUserLocation(it)
      realMoveToUserLocation(it, animating = false)
    }
  }

  override fun updateUserLocation(geoPoint: MyGeoPoint) {
    val userLocation = requireUserLocationCollection()
    val newPlacemark = userLocation.addPlacemark(
      Point(geoPoint.latitude, geoPoint.longitude),
      fromDrawable(context, R.drawable.user_geo)
    )

    handler.postDelayed(
      {
        userLocationPlacemark?.let { userLocation.remove(it) }
        userLocationPlacemark = newPlacemark
      },
      10L
    )
  }

  override fun showRoute(route: Route) {
    removeRoute()
    val collection = requireRoutesCollection()
    route.metadata.estimation
    routePolyline = collection.addPolyline(route.geometry).also {
      it.setStrokeColor(context.colorAttr(R.attr.textMain))
      it.gapLength = 5f
      it.strokeWidth = 2f
    }
    val badge = walkRouteInfoView.apply {
      text = route.metadata.weight.time.text
    }.asBitmap()
    val badgeGeo = route.geometry.points[route.geometry.points.size / 2]
    badgeObject = collection.addPlacemark(badgeGeo, ImageProvider.fromBitmap(badge))
  }

  override fun removeRoute() {
    try {
      routePolyline?.let { requireRoutesCollection().remove(it) }
      badgeObject?.let { requireRoutesCollection().remove(it) }
    } catch (t: Throwable) {
      // ignore
    }
  }

  override fun moveToUserLocation(geoPoint: MyGeoPoint) {
    realMoveToUserLocation(geoPoint, animating = true)
  }

  private fun realMoveToUserLocation(geoPoint: MyGeoPoint, animating: Boolean) {
    val mapView = requireMapView()

    if (animating) {
      mapView.map.move(
        CameraPosition(Point(geoPoint.latitude, geoPoint.longitude), DEFAULT_ZOOM, 0f, 0f),
        Animation(Animation.Type.SMOOTH, 0.3f),
        null
      )
    } else {
      mapView.map.move(CameraPosition(Point(geoPoint.latitude, geoPoint.longitude), DEFAULT_ZOOM, 0f, 0f))
    }
  }

  override fun addPlacemark(id: String, geoPoint: MyGeoPoint, drawableInt: Int) {
    val placemark = idToPlaceMarks[id]
    if (placemark == null) {
      realAddPlacemark(id, geoPoint, drawableInt)
    } else {
      requireMapObjectsCollection().remove(placemark)
      realAddPlacemark(id, geoPoint, drawableInt)
    }
  }

  private fun realAddPlacemark(
    id: String,
    geoPoint: MyGeoPoint,
    drawableInt: Int
  ) {
    idToPlaceMarks[id] = requireMapObjectsCollection().addPlacemark(
      Point(geoPoint.latitude, geoPoint.longitude),
      ImageProvider.fromBitmap(AppCompatResources.getDrawable(context, drawableInt)?.toBitmap())
    ).apply {
      placeMarksToId[this] = id
      addTapListener(placeMarkTapListener)
    }
  }

  private fun requireMapObjectsCollection(): MapObjectCollection {
    return mapObjects ?: (requireMapView().map.mapObjects).addCollection().also { mapObjects = it }
  }

  private fun requireUserLocationCollection(): MapObjectCollection {
    return userLocation ?: (requireMapView().map.mapObjects).addCollection().also { userLocation = it }
  }

  private fun requireRoutesCollection(): MapObjectCollection {
    return routesCollection ?: (requireMapView().map.mapObjects).addCollection().also { routesCollection = it }
  }

  private fun requireMapView(): MapView {
    return map ?: throw MapNotAttachedToWindowException()
  }

  override fun detach() {
    idToPlaceMarks.values.forEach { mapObjects?.remove(it) }
    idToPlaceMarks.clear()
    map = null
    mapObjects = null
    routesCollection = null
  }

  companion object {

    private const val DEFAULT_ZOOM = 17F
  }
}