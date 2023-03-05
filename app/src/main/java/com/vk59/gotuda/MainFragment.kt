package com.vk59.gotuda

import android.Manifest.permission
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vk59.gotuda.MapViewType.MAPKIT
import com.vk59.gotuda.MapViewType.OSM
import com.vk59.gotuda.databinding.FragmentMainBinding
import com.yandex.mapkit.Animation
import com.yandex.mapkit.Animation.Type.SMOOTH
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapType.MAP
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MainFragment : Fragment(R.layout.fragment_main), UserLocationObjectListener {

  private val binding: FragmentMainBinding by viewBinding(FragmentMainBinding::bind)

  private val viewModel: MainViewModel by viewModels()

  private var userLocationLayer: UserLocationLayer? = null

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initBottomButtons()
    initMap()
  }

  private fun getApplicationContext(): Context {
    return requireContext().applicationContext
  }

  private fun initBottomButtons() {
    viewModel.listenToButtons().observe(viewLifecycleOwner) { buttons ->
      binding.buttonList.addButtons(buttons)
    }
  }

  private fun initMap() {
    initAllMaps()
    viewModel.mapViewType.observe(viewLifecycleOwner) { mapType ->
      when (mapType) {
        OSM -> {
          binding.mapKit.isVisible = false
          binding.mapView.isVisible = true
        }
        MAPKIT -> {
          binding.mapKit.isVisible = true
          binding.mapView.isVisible = false
        }
        else -> { /* Nothing to do */
        }
      }
    }
  }

  private fun initAllMaps() {
    initOsm()
    initMapKit()

    val locationManager: LocationManager? = getSystemService(requireContext(), LocationManager::class.java)
    locationManager?.let {
      if (VERSION.SDK_INT >= VERSION_CODES.P) {
        initLocationManager(it)
      } else {
        initLocationManagerLessP(it)
      }
    }
  }

  private var mapObjectCollection: MapObjectCollection? = null

  private fun initMapKit() {
    binding.mapKit.map.mapType = MAP
    if (requireContext().resources.configuration.uiMode and UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES) {
      binding.mapKit.map.isNightModeEnabled = true
    }

    val mapKit = MapKitFactory.getInstance()
    mapKit.resetLocationManagerToDefault()
    userLocationLayer = mapKit.createUserLocationLayer(binding.mapKit.mapWindow)
    userLocationLayer?.isVisible = true
    userLocationLayer?.isHeadingEnabled = true

    userLocationLayer?.setObjectListener(this)
  }

  private fun initOsm() {
    Configuration.getInstance().load(
      requireContext().applicationContext,
      PreferenceManager.getDefaultSharedPreferences(requireContext().applicationContext)
    )
    val map = binding.mapView
    map.setTileSource(MAPNIK)
    val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), binding.mapView)
    locationOverlay.setPersonIcon(getDrawable(requireContext(), R.drawable.user_geo)?.toBitmap())
    locationOverlay.setDirectionIcon(getDrawable(requireContext(), R.drawable.user_geo)?.toBitmap())
    locationOverlay.enableMyLocation()
    binding.mapView.overlays.add(locationOverlay)
  }

  @RequiresApi(VERSION_CODES.P)
  private fun initLocationManager(manager: LocationManager) {
    if (manager.isLocationEnabled) {
      initLocationManagerLessP(manager)
    }
  }

  override fun onStart() {
    MapKitFactory.getInstance().onStart()
    binding.mapKit.onStart()
    super.onStart()
  }

  override fun onResume() {
    super.onResume()
    binding.mapView.onResume()
  }

  override fun onPause() {
    super.onPause()
    Configuration.getInstance().save(
      getApplicationContext(),
      PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
    )
    binding.mapView.onPause()
  }

  override fun onStop() {
    binding.mapKit.onStop()
    MapKitFactory.getInstance().onStop()
    super.onStop()
  }

  override fun onDestroy() {
    super.onDestroy()
    binding.mapView.onDetach()
  }

  private fun initLocationManagerLessP(manager: LocationManager) {
    if (ActivityCompat.checkSelfPermission(
        requireContext(),
        permission.ACCESS_FINE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
        requireContext(),
        permission.ACCESS_COARSE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      registerForActivityResult(
        RequestMultiplePermissions(),
      ) { isGranted ->
        if (isGranted.values.all { it }) {
          requestGeoUpdates(manager)
        } else {
          Toast.makeText(requireContext(), "Permission denied :(", Toast.LENGTH_LONG).show()
        }
      }.launch(arrayOf(permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION))
    } else {
      requestGeoUpdates(manager)
    }
  }

  @RequiresPermission(allOf = [permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION])
  private fun requestGeoUpdates(manager: LocationManager) {
    manager.requestLocationUpdates(GPS_PROVIDER, 1000, 10f, onLocationChanged(binding.mapView, binding.mapKit))
  }

  private fun onLocationChanged(mapView: MapView, mapKit: com.yandex.mapkit.mapview.MapView) =
    LocationListener { location ->
      // TODO: Remove one of types
      // 1. OSM
      val geoPoint = GeoPoint(location.latitude, location.longitude)
      mapView.controller.animateTo(geoPoint, 18.0, 1000)

      // 2. MapKit
      val point = Point(location.latitude, location.longitude)
      mapKit.map.move(
        CameraPosition(point, 18.0f, 0.0f, 0.0f),
        Animation(SMOOTH, 0f),
        null
      )
//
//      mapKitLocation?.let { mapObjectCollection?.remove(it) }
//      if (mapObjectCollection == null) {
//        mapObjectCollection = binding.mapKit.map.mapObjects.addCollection()
//      }
//      mapKitLocation = mapObjectCollection?.addPlacemark(point)
//      mapKitLocation?.setIcon(ImageProvider.fromResource(context, R.drawable.user_geo_png))
//      mapKitLocation?.isDraggable = true
//
//      Timber.tag(TAG).i(mapObjectCollection.toString())
    }

  private var mapKitLocation: PlacemarkMapObject? = null

  companion object {

    private const val REQUEST_PERMISSIONS_REQUEST_CODE = 1

    private const val TAG = "MainFragment"
  }

  override fun onObjectAdded(userLocationView: UserLocationView) {
    userLocationView.pin.setIcon(
      ImageProvider.fromResource(requireContext(), R.drawable.user_geo)
    )
  }

  override fun onObjectRemoved(p0: UserLocationView) {
  }

  override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {
  }
}